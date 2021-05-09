package com.trxmon.batch.configuration.writer;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.batch.item.validator.ValidationException;

public class AlertItemWriter<Alert> implements ItemWriter<Alert> {

    private static final Logger log = LoggerFactory.getLogger(AlertItemWriter.class);

    private StepExecution stepExecution;

    @Override
    public void write(List<? extends Alert> list) throws Exception {
        JobParameters parameters = stepExecution.getJobExecution().getJobParameters();
        String tName = (Thread.currentThread().getName());

        String tmpdir = System.getProperty("java.io.tmpdir");
        String outFile = tName + ".out";

        for (Alert alert : list) {
            System.out.println("[" + tName + "] " + alert.toString() + ", stored in: "
                    + tmpdir + outFile);

            if (13 == ((com.trxmon.batch.domain.Alert)alert).getId()) {
                throw new ValidationException("Simulated error when writing alert: " + alert + " to "
                        + tmpdir + outFile);
            }

            Files.writeString(
                    Path.of(tmpdir, outFile),
                    alert + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND
            );
        }

        ExecutionContext stepContext = this.stepExecution.getExecutionContext();
        int count = stepContext.containsKey("count") ? stepContext.getInt("count") : 0;
        stepContext.put("count", (count + list.size()));
        stepContext.put("aaa", 2);
        //System.out.println(tName + ", count=" + (count + list.size()) + ", list.size="  + list.size());
    }

    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }
}
