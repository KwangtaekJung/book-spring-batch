package com.example.Chapter4.jobs;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@EnableBatchProcessing
//@SpringBootApplication
public class MethodInvokingTaskletConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job methodInvokingJob() {
        return this.jobBuilderFactory.get("methodInvokingJob")
                .start(methodInvokingStep())
                .incrementer(new DailyJobTimestamper())
                .build();
    }

    @Bean
    public Step methodInvokingStep() {
        return this.stepBuilderFactory.get("methodInvokingStep")
//                .tasklet(methodInvokingTasklet())
                .tasklet(methodInvokingTaskletWithParams(null))
                .build();
    }

    @Bean
    public MethodInvokingTaskletAdapter methodInvokingTasklet() {
        MethodInvokingTaskletAdapter methodInvokingTaskletAdapter = new MethodInvokingTaskletAdapter();

        methodInvokingTaskletAdapter.setTargetObject(service());
        methodInvokingTaskletAdapter.setTargetMethod("serviceMethod");
        return methodInvokingTaskletAdapter;
    }

    @StepScope
    @Bean
    public MethodInvokingTaskletAdapter methodInvokingTaskletWithParams(
            @Value("#{jobParameters['message']}") String message ) {
        MethodInvokingTaskletAdapter methodInvokingTaskletAdapter = new MethodInvokingTaskletAdapter();

        methodInvokingTaskletAdapter.setTargetObject(service());
        methodInvokingTaskletAdapter.setTargetMethod("serviceMethodWithParams");
        methodInvokingTaskletAdapter.setArguments(new String[] {message});
        return methodInvokingTaskletAdapter;
    }

    @Bean
    public CustomService service() {
       return new CustomService();
    }

    public static void main(String[] args) {
        SpringApplication.run(MethodInvokingTaskletConfiguration.class, args);
    }
}
