package com.example.chapter7.batch;

import com.example.chapter7.domain.CustomerEntity;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.HibernateCursorItemReader;
import org.springframework.batch.item.database.HibernatePagingItemReader;
import org.springframework.batch.item.database.builder.HibernateCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.HibernatePagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.Collections;

@RequiredArgsConstructor
@Configuration
public class HibernatePagingJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public HibernatePagingItemReader<CustomerEntity> customerItemReader(
            EntityManagerFactory entityManagerFactory,
            @Value("#{jobParameters['city']}") String city) {

        city = "Chicago";
        return new HibernatePagingItemReaderBuilder<CustomerEntity>()
                .name("customerItemReader")
                .sessionFactory(entityManagerFactory.unwrap(SessionFactory.class))
                .queryString("from CustomerEntity where city = :city")
                .parameterValues(Collections.singletonMap("city", city))
                .pageSize(10)
                .build();
    }

    @Bean
    public ItemWriter<CustomerEntity> itemWriter() {
        return (items) -> items.forEach(System.out::println);
    }

    @Bean
    public Step copyFileStep() {
        return this.stepBuilderFactory.get("copyFileStep")
                .<CustomerEntity, CustomerEntity>chunk(10)
                .reader(customerItemReader(null, null))
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("HibernatePagingJob")
                .start(copyFileStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }
}
