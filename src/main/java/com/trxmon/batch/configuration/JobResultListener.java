package com.trxmon.batch.configuration;

import com.trxmon.batch.BatchApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import org.joda.time.DateTime;
import org.springframework.batch.core.JobParameters;

import java.util.List;
import java.util.UUID;


public class JobResultListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(JobResultListener.class);

    private DateTime startTime, stopTime;

    public void beforeJob(JobExecution jobExecution) {
        startTime = new DateTime();
        //String jobID = jobExecution.getJobParameters().getString("JobID");
        String alert_path = jobExecution.getJobParameters().getString("alert_path");
        String alert_date = jobExecution.getJobParameters().getString("alert_date");
        log.info("Job started at " + startTime + " with parameters: "
                + "alert_path=" + alert_path
                + ", alert_date=" + alert_date);
    }

    public void afterJob(JobExecution jobExecution) {
        stopTime = new DateTime();
        //String jobID = jobExecution.getJobParameters().getString("JobID");
        String alert_path = jobExecution.getJobParameters().getString("path");
        String alert_date = jobExecution.getJobParameters().getString("date");

        log.info("Job ended at " + stopTime
                + ", execution time: " + (stopTime.getMillis() - startTime.getMillis())
                + ", status: " + jobExecution.getStatus()
                + ", parameters: "
                + "alert_path=" + alert_path
                + ", alert_date=" + alert_date);

        JobParameters parameters = jobExecution.getJobParameters();

        /*
        if(jobExecution.getStatus() == BatchStatus.FAILED){
            System.err.println("Job failed with exceptions:");
            List<Throwable> exceptionList = jobExecution.getAllFailureExceptions();
            for(Throwable the : exceptionList){
                System.err.println("Exception: " + the.getLocalizedMessage());
            }
        }
        */
    }
}