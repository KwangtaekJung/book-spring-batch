package com.example;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzJobConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job scheduledJob() {
        return this.jobBuilderFactory.get("scheduledJob")
                .incrementer(new RunIdIncrementer())
                .start(scheduledStep1())
                .build();
    }

    @Bean
    public Step scheduledStep1() {
       return this.stepBuilderFactory.get("scheduled Step1")
               .tasklet((stepContribution, chunkContext) -> {
                   System.out.println("scheduled step1 ran!");
                   return RepeatStatus.FINISHED;
               }).build();
    }
}
