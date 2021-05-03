package com.trxmon.batch.configuration;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

import java.util.List;
import org.slf4j.Logger;

public class AlertItemWriter<Alert> implements ItemWriter<Alert> {

    private static final Logger log = LoggerFactory.getLogger(AlertItemWriter.class);

    private StepExecution stepExecution;

    @Override
    public void write(List<? extends Alert> list) throws Exception {
            JobParameters parameters = stepExecution.getJobExecution().getJobParameters();
            String jobID = parameters.getString("JobID");
            for (Alert alert : list) {
                System.out.println(jobID + ": " + alert.toString());
            }
            ExecutionContext stepContext = this.stepExecution.getExecutionContext();
            int count = stepContext.containsKey("count") ? stepContext.getInt("count") : 0;
            System.out.println("count=" + (count + list.size()) + ", list.size="  + list.size());
            stepContext.put("count", (count + list.size()));
    }

    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }
}
