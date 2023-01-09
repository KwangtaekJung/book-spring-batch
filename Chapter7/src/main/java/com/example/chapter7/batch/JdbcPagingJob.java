package com.example.chapter7.batch;

import com.example.chapter7.domain.CustomerDomain;
import com.example.chapter7.mapper.CustomerRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
//@Configuration
public class JdbcPagingJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public JdbcPagingItemReader<CustomerDomain> customerItemReader(
            DataSource dataSource,
            PagingQueryProvider queryProvider,
            @Value("#{jobParameters['city']}") String city) {

        city = "Chicago"; //Test
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("city", city);

        return new JdbcPagingItemReaderBuilder<CustomerDomain>()
                .name("customerItemReader")
                .dataSource(dataSource)
                .queryProvider(queryProvider)
                .parameterValues(parameterValues)
                .pageSize(10)
                .rowMapper(new CustomerRowMapper())
                .build();
    }

    @Bean
    public SqlPagingQueryProviderFactoryBean pagingQueryProviderFactoryBean(DataSource dataSource) {
        SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();

        factoryBean.setDataSource(dataSource);
        factoryBean.setSelectClause("select *");
        factoryBean.setFromClause("from Customer");
        factoryBean.setWhereClause("where city = :city");
        factoryBean.setSortKey("lastName");

        return factoryBean;
    }

    @Bean
    public ItemWriter<CustomerDomain> itemWriter() {
        return (items) -> items.forEach(System.out::println);
    }

    @Bean
    public Step copyFileStep() {
        return this.stepBuilderFactory.get("copyFileStep")
                .<CustomerDomain, CustomerDomain>chunk(10)
                .reader(customerItemReader(null, null, null))
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("JdbcPagingJob")
                .start(copyFileStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }
}
