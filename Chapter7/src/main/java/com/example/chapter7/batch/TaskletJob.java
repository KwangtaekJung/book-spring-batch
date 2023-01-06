package com.example.chapter7.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
//@Configuration
public class TaskletJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("taskletJob")
                .start(taskletStep1())
                .build();
    }

    @Bean
    public Step taskletStep1() {
        return this.stepBuilderFactory.get("taskletStep1")
                .tasklet(((stepContribution, chunkContext) -> {
                    log.debug("-> job -> [step1]");
                    return RepeatStatus.FINISHED;
                })).build();
    }
}
