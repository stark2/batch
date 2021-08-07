package com.trxmon.batch.configuration;

import com.trxmon.batch.filter.AlertItemFilter;
import com.trxmon.batch.listener.JobResultListener;
import com.trxmon.batch.mapper.AlertFieldSetMapper;
import com.trxmon.batch.mapper.JsonLineTokenizer;
import com.trxmon.batch.processor.AlertItemProcessor;
import com.trxmon.batch.processor.AlertItemProcessor2;
import com.trxmon.batch.processor.CleanupTask;
import com.trxmon.batch.validator.AlertItemValidator;
import com.trxmon.batch.validator.ParameterValidator;
import com.trxmon.batch.writer.AlertItemWriter;
import com.trxmon.batch.domain.*;

import io.leego.banana.BananaUtils;
import io.leego.banana.Font;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
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
import org.springframework.core.task.SyncTaskExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class JobConfiguration {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Value("classpath*:data/alert*.json")
    private Resource[] inputFiles;

    @Value("${welcomeMessage}")
    private String welcomeMessage;

    @Bean
    public MultiResourceItemReader<Alert> multiResourceItemReader() throws InterruptedException {
        MultiResourceItemReader<Alert> reader = new MultiResourceItemReader<>();
        reader.setDelegate(alertFlatFileItemReader());
        reader.setResources(inputFiles);
        return reader;
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

    /*
    @Bean
    @StepScope
    public TaskExecutor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(inputFiles.length);
        executor.setMaxPoolSize(inputFiles.length);
        executor.setThreadNamePrefix("multi-thread-");
        executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);
        executor.initialize();
        executor.afterPropertiesSet();
        return executor;
    }

    @Bean
    public Partitioner partitioner() {
        MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
        partitioner.setResources(inputFiles);
        partitioner.setKeyName("file");
        //partitioner.partition(10);
        return partitioner;
    }

    @Bean
    public Step masterStep() throws Exception {
        return stepBuilderFactory.get("masterStep")
                .partitioner(step1().getName(), partitioner())
                .step(step1())
                .gridSize(inputFiles.length)
                .taskExecutor(executor())
                .build();
    }
    */

    @Bean
    public Step step1() throws Exception {
        return stepBuilderFactory.get("step1")
                .<Alert, Alert>chunk(1)
                .reader(multiResourceItemReader())  //alertItemReader()
                .processor(compositeItemProcessor())  //alertItemProcessor()
                .writer(alertItemWriter())
                .listener(promotionListener())
                .taskExecutor(new SyncTaskExecutor())
                .build();
    }

    @Bean
    public Step step2() throws Exception {
        return stepBuilderFactory.get("step2")
                .tasklet(new CleanupTask())
                .taskExecutor(new SyncTaskExecutor())
                .build();
    }

    @Bean
    public ExecutionContextPromotionListener promotionListener() {
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[] {"count", "aaa"});
        return listener;
    }

    /*
    @Bean
    public JobParametersValidator validator() {
        DefaultJobParametersValidator validator = new DefaultJobParametersValidator();
        validator.setRequiredKeys(new String[]{"alert_path"});
        validator.setRequiredKeys(new String[]{"alert_date"});
        return validator;
    }
    */

    @Bean
    public CompositeJobParametersValidator validator() {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        DefaultJobParametersValidator defaultJobParametersValidator =
                new DefaultJobParametersValidator(new String[]{"alert_path", "alert_date"}, new String[]{});
        defaultJobParametersValidator.afterPropertiesSet();
        validator.setValidators(Arrays.asList(defaultJobParametersValidator, new ParameterValidator()));
        return validator;
    }

    @Bean
    public Job job() throws Exception {
        System.out.println(BananaUtils.bananaify(welcomeMessage, Font.ANSI_SHADOW));

        return jobBuilderFactory.get("job")
                .validator(validator())
                .incrementer(new RunIdIncrementer())
                .listener(new JobResultListener())
                .start(step1())
                .next(step2())
                .build();
    }
}
