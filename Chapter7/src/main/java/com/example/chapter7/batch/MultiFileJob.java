package com.example.chapter7.batch;

import com.example.chapter7.domain.CustomerWithTransactions;
import com.example.chapter7.mapper.TransactionFieldSetMapper;
import com.example.chapter7.util.CustomerMultiFileReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
//@Configuration
public class MultiFileJob {
    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public MultiResourceItemReader multiCustomerReader(
            @Value("#{jobParameters['customerFile']}") Resource[] inputFiles) throws IOException {

        Resource[] inputFilesLocalTest = new PathMatchingResourcePatternResolver().getResources("input/customerMultiFormat*");

        return new MultiResourceItemReaderBuilder<>()
                .name("multiCustomerReader")
                .resources(inputFilesLocalTest)
                .delegate(customerMultiFileReader())
                .build();
    }

    @Bean
    public CustomerMultiFileReader customerMultiFileReader() {
        return new CustomerMultiFileReader(customerItemReader());
    }

    @Bean
    public FlatFileItemReader customerItemReader() {
        return new FlatFileItemReaderBuilder<CustomerWithTransactions>()
                .name("customerItemReader")
                .lineMapper(lineTokenizer())
                .build();
    }

    @Bean
    public PatternMatchingCompositeLineMapper lineTokenizer() {
        Map<String, LineTokenizer> lineTokenizers =
                new HashMap<>(2);

        lineTokenizers.put("CUST*", customerLineTokenizer());
        lineTokenizers.put("TRANS*", transactionLineTokenizer());

        Map<String, FieldSetMapper> fieldSetMappers =
                new HashMap<>(2);

        BeanWrapperFieldSetMapper<CustomerWithTransactions> customerFieldSetMapper =
                new BeanWrapperFieldSetMapper<>();
        customerFieldSetMapper.setTargetType(CustomerWithTransactions.class);

        fieldSetMappers.put("CUST*", customerFieldSetMapper);
        fieldSetMappers.put("TRANS*", new TransactionFieldSetMapper());

        PatternMatchingCompositeLineMapper lineMappers =
                new PatternMatchingCompositeLineMapper();

        lineMappers.setTokenizers(lineTokenizers);
        lineMappers.setFieldSetMappers(fieldSetMappers);

        return lineMappers;
    }

    @Bean
    public DelimitedLineTokenizer transactionLineTokenizer() {
        DelimitedLineTokenizer lineTokenizer =
                new DelimitedLineTokenizer();

        lineTokenizer.setNames("prefix",
                "accountNumber",
                "transactionDate",
                "amount");

        return lineTokenizer;
    }

    @Bean
    public DelimitedLineTokenizer customerLineTokenizer() {
        DelimitedLineTokenizer lineTokenizer =
                new DelimitedLineTokenizer();

        lineTokenizer.setNames("firstName",
                "middleInitial",
                "lastName",
                "address",
                "city",
                "state",
                "zipCode");

        lineTokenizer.setIncludedFields(1, 2, 3, 4, 5, 6, 7);

        return lineTokenizer;
    }

    @Bean
    public ItemWriter itemWriter() {
        return (items) -> items.forEach(System.out::println);
    }

    @Bean
    public Step copyFileStep() throws IOException {
        return this.stepBuilderFactory.get("copyFileStep")
                .<CustomerWithTransactions, CustomerWithTransactions>chunk(10)
                .reader(multiCustomerReader(null))
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job() throws IOException {
        return this.jobBuilderFactory.get("job")
                .start(copyFileStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }
}
