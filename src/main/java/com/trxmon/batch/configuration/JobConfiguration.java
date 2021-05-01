package com.trxmon.batch.configuration;

import com.trxmon.batch.domain.Alert;
import com.trxmon.batch.domain.AlertFieldSetMapper;

import io.leego.banana.BananaUtils;
import io.leego.banana.Font;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public FlatFileItemReader<Alert> alertItemReader() {

        FlatFileItemReader<Alert> reader = new FlatFileItemReader<>();

        reader.setLinesToSkip(1);
        reader.setResource(new ClassPathResource("data/alerts.csv"));

        DefaultLineMapper<Alert> alertLineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(new String[] {"alert_id", "alert_type", "alert_description", "alert_date"});

        alertLineMapper.setLineTokenizer(tokenizer);
        alertLineMapper.setFieldSetMapper(new AlertFieldSetMapper());
        alertLineMapper.afterPropertiesSet();

        reader.setLineMapper(alertLineMapper);

        return reader;
    }

    @Bean
    public ItemWriter<Alert> alertItemWriter() {

        return items -> {
            for (Alert item : items) {
                System.out.println(item.toString());
            }
        };
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<Alert, Alert>chunk(2)
                .reader(alertItemReader())
                .writer(alertItemWriter())
                .build();
    }

    @Bean
    public Job job() {
        System.out.println(BananaUtils.bananaify("Spring Batch Job", Font.ANSI_SHADOW));
        return jobBuilderFactory.get("job").start(step1()).build();
    }
}
