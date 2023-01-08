package com.example.chapter7.batch;

import com.example.chapter7.domain.Customer;
import com.example.chapter7.domain.CustomerWithTransactions;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.text.SimpleDateFormat;

@Slf4j
@RequiredArgsConstructor
//@Configuration
public class JsonJob {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public JsonItemReader<CustomerWithTransactions> customerFileReader(
            @Value("#{jobParameters['customerFile']}")Resource inputFile) {

        Resource inputFileLocalTest = new ClassPathResource("input/customer.json");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));

        JacksonJsonObjectReader<CustomerWithTransactions> jsonObjectReader =
                new JacksonJsonObjectReader<>(CustomerWithTransactions.class);
        jsonObjectReader.setMapper(objectMapper);

        return new JsonItemReaderBuilder<CustomerWithTransactions>()
                .name("customerFileReader")
                .jsonObjectReader(jsonObjectReader)
                .resource(inputFileLocalTest)
                .build();
    }

    @Bean
    public ItemWriter itemWriter() {
        return (items -> items.forEach(System.out::println));
    }

    @Bean
    public Step copyFileStep() {
        return this.stepBuilderFactory.get("copyFileStep")
                .<CustomerWithTransactions, CustomerWithTransactions> chunk(10)
                .reader(customerFileReader(null))
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("JsonJob")
                .start(copyFileStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

}
