package com.example.Chapter4.chunk;

import com.example.Chapter4.jobs.DailyJobTimestamper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

@EnableBatchProcessing
//@SpringBootApplication
public class BatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job chunkBasicJob() {
        return this.jobBuilderFactory.get("chunkBasicJob")
                .start(chunkBasicStep())
                .incrementer(new DailyJobTimestamper())
                .build();
    }

    @Bean
    public Step chunkBasicStep() {
        return this.stepBuilderFactory.get("chunkBasicStep")
                .<String, String>chunk(10)
                .reader(itemReaderBasic(null))
                .writer(itemWriterBasic(null))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<String> itemReaderBasic(
            @Value("#{jobParameters['inputFile']}") Resource inputFile) {
        return new FlatFileItemReaderBuilder<String>()
                .name("itemReader")
                .resource(inputFile)
                .lineMapper(new PassThroughLineMapper())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<String> itemWriterBasic(
            @Value("#{jobParameters['outputFile']}") Resource outputFile) {
        return new FlatFileItemWriterBuilder<String>()
                .name("itemWriter")
                .resource(outputFile)
                .lineAggregator(new PassThroughLineAggregator<>())
                .build();
    }
}
