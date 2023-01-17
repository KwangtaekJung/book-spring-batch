package com.example.chapter7.batch;

import com.example.chapter7.domain.CustomerWithAddressNumber;
import com.example.chapter7.util.CustomerItemListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.Arrays;
import java.util.List;

@EnableBatchProcessing
@SpringBootApplication
@RequiredArgsConstructor
public class FixedWidthWithListenerJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public FlatFileItemReader<CustomerWithAddressNumber> customerItemReader(
            @Value("#{jobParameters['customerFile']}") Resource inputFile) {

        return new FlatFileItemReaderBuilder<CustomerWithAddressNumber>()
                .name("customerItemReader")
                .resource(inputFile)
                .fixedLength()
                .columns(new Range[]{new Range(1, 11), new Range(12, 12), new Range(13, 22),
                        new Range(23, 26), new Range(27, 46), new Range(47, 62), new Range(63, 64),
                        new Range(65, 69)})
                .names(new String[]{"firstName", "middleInitial", "lastName",
                        "addressNumber", "street", "city", "state", "zipCode"})
                .targetType(CustomerWithAddressNumber.class)
                .build();
    }

    @Bean
    public ItemWriter<CustomerWithAddressNumber> itemWriter() {
        return (items) -> items.forEach(System.out::println);
    }

    @Bean
    public CustomerItemListener customerItemListener() {
        return new CustomerItemListener();
    }

    @Bean
    public Step copyFileStep() {
        return this.stepBuilderFactory.get("copyFileStep")
                .<CustomerWithAddressNumber, CustomerWithAddressNumber>chunk(10)
                .reader(customerItemReader(null))
                .writer(itemWriter())
                .faultTolerant()
                .skipLimit(100)
                .skip(Exception.class)
                .listener(customerItemListener())
                .build();
    }

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("fixedWithJob")
                .start(copyFileStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    public static void main(String[] args) {
        List<String> realArgs = Arrays.asList("customerFile=/input/customerFixedWidth.txt");

        SpringApplication.run(FixedWidthWithListenerJob.class, realArgs.toArray(new String[1]));
    }
}
