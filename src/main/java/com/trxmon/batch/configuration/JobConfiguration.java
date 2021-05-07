package com.trxmon.batch.configuration;

import com.trxmon.batch.domain.*;

import io.leego.banana.BananaUtils;
import io.leego.banana.Font;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.SyncTaskExecutor;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class JobConfiguration {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Value("classpath*:data/alert*.json")
    private Resource[] inputFiles;

    @Bean
    public MultiResourceItemReader<Alert> multiResourceItemReader() {
        MultiResourceItemReader<Alert> reader = new MultiResourceItemReader<>();
        reader.setDelegate(alertFlatFileItemReader());
        reader.setResources(inputFiles);
        return reader;
    }

    @Bean
    public Partitioner partitioner() {
        MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
        partitioner.setResources(inputFiles);
        partitioner.setKeyName("file");
        return partitioner;
    }

    @Bean
    public FlatFileItemReader<Alert> alertFlatFileItemReader() {
        FlatFileItemReader<Alert> reader = new FlatFileItemReader<>();
        reader.setSaveState(true);
        //reader.setLinesToSkip(1);
        //reader.setResource(new ClassPathResource("data/alert1.csv"));
        DefaultLineMapper<Alert> alertLineMapper = new DefaultLineMapper<>();
        //DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        //tokenizer.setNames(new String[] {"alert_id", "alert_type", "alert_description", "alert_date"});
        JsonLineTokenizer tokenizer = new JsonLineTokenizer();
        alertLineMapper.setLineTokenizer(tokenizer);
        alertLineMapper.setFieldSetMapper(new AlertFieldSetMapper());
        alertLineMapper.afterPropertiesSet();
        reader.setLineMapper(alertLineMapper);
        return reader;
    }

    @Bean
    public AlertItemProcessor alertItemProcessor() {
        return new AlertItemProcessor();
    }

    @Bean
    public ValidatingItemProcessor validatingItemProcessor() {
        ValidatingItemProcessor<Alert> alertValidatingItemProcessor =
                new ValidatingItemProcessor<>(new AlertItemValidator());
        //alertValidatingItemProcessor.setFilter(true);
        return alertValidatingItemProcessor;
    }

    @Bean
    public CompositeItemProcessor<Alert, Alert> compositeItemProcessor() throws Exception {
        List<ItemProcessor<Alert, Alert>> delegates = new ArrayList<>(4);
        delegates.add(new AlertItemProcessor());
        delegates.add(new AlertItemProcessor2());
        delegates.add(validatingItemProcessor());
        delegates.add(new AlertItemFilter());

        CompositeItemProcessor<Alert, Alert> compositeItemProcessor = new CompositeItemProcessor<>();
        compositeItemProcessor.setDelegates(delegates);
        compositeItemProcessor.afterPropertiesSet();

        return compositeItemProcessor;
    }

    @Bean
    public AlertItemWriter<Alert> alertItemWriter() {
        return new AlertItemWriter<Alert>();
    }

    @Bean
    public SimpleAsyncTaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    public Step masterStep() throws Exception {
        return stepBuilderFactory.get("masterStep")
                .partitioner(slaveStep1().getName(), partitioner())
                .step(slaveStep1())
                .gridSize(4)
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Step slaveStep1() throws Exception {
        return stepBuilderFactory.get("step1")
                .<Alert, Alert>chunk(2)
                .reader(multiResourceItemReader())  //alertItemReader()
                .processor(compositeItemProcessor())  //alertItemProcessor()
                .writer(alertItemWriter())
                .listener(promotionListener())
                .build();
    }

    @Bean
    public Step step2() throws Exception {
        return stepBuilderFactory.get("step2")
                .tasklet(new EnrichmentTask())
                .taskExecutor(new SyncTaskExecutor())
                .build();
    }

    @Bean
    public ExecutionContextPromotionListener promotionListener() {
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[] {"count"});
        return listener;
    }
    @Bean
    public Job job() throws Exception {
        System.out.println(BananaUtils.bananaify("Spring Batch", Font.ANSI_SHADOW));
        return jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .listener(new JobResultListener())
                .start(masterStep())
                .next(step2())
                .build();
    }
}
