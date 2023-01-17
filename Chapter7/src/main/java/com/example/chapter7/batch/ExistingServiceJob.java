package com.example.chapter7.batch;

import com.example.chapter7.domain.CustomerEntity;
import com.example.chapter7.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
//@Configuration
public class ExistingServiceJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public ItemReaderAdapter<CustomerEntity> customerItemReader(CustomerService customerService) {
        ItemReaderAdapter<CustomerEntity> adapter = new ItemReaderAdapter<>();

        adapter.setTargetObject(customerService);
        adapter.setTargetMethod("getCustomer");

        return adapter;
    }

    @Bean
    public ItemWriter<CustomerEntity> itemWriter() {
        return (items) -> items.forEach(System.out::println);
    }

    @Bean
    public Step copyFileStep() {
        return this.stepBuilderFactory.get("copyFileStep")
                .<CustomerEntity, CustomerEntity>chunk(10)
                .reader(customerItemReader(null ))
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("ExistingServiceJob")
                .start(copyFileStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }
}
