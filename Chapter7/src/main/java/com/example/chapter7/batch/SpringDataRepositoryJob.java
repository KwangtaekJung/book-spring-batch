package com.example.chapter7.batch;

import com.example.chapter7.domain.CustomerEntity;
import com.example.chapter7.util.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Collections;

@RequiredArgsConstructor
@Configuration
public class SpringDataRepositoryJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public RepositoryItemReader<CustomerEntity> customerItemReader(
            CustomerRepository repository,
            @Value("jobParameters['city']}") String city) {

        city = "Chicago";
        return new RepositoryItemReaderBuilder<CustomerEntity>()
                .name("customerItemReader")
                .arguments(Collections.singleton(city))
                .methodName("findByCity")
                .repository(repository)
                .sorts(Collections.singletonMap("lastName", Sort.Direction.ASC))
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
        return this.jobBuilderFactory.get("RepositoryJob")
                .start(copyFileStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }
}
