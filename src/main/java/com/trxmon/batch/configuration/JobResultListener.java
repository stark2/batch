package com.trxmon.batch.configuration;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import org.joda.time.DateTime;
import org.springframework.batch.core.JobParameters;

import java.util.List;
import java.util.UUID;


public class JobResultListener implements JobExecutionListener {

    private DateTime startTime, stopTime;

    public void beforeJob(JobExecution jobExecution) {
        startTime = new DateTime();
        String jobID = jobExecution.getJobParameters().getString("JobID");
        System.out.println("Job " + jobID + " started at: " + startTime);
    }

    public void afterJob(JobExecution jobExecution) {
        stopTime = new DateTime();
        String jobID = jobExecution.getJobParameters().getString("JobID");
        System.out.println("Job " + jobID + " ended at: " + stopTime
                + ", execution time: " + (stopTime.getMillis() - startTime.getMillis())
                + ", status: " + jobExecution.getStatus());

        JobParameters parameters = jobExecution.getJobParameters();

        if(jobExecution.getStatus() == BatchStatus.FAILED){
            System.err.println("Job failed with exceptions:");
            List<Throwable> exceptionList = jobExecution.getAllFailureExceptions();
            for(Throwable the : exceptionList){
                System.err.println("Exception: " + the.getLocalizedMessage());
            }
        }
    }
}