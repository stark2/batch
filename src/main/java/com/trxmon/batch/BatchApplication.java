package com.trxmon.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@SpringBootApplication
@EnableBatchProcessing
public class BatchApplication implements CommandLineRunner {

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	Job job;

	public static void main(String[] args) {
		SpringApplication.run(BatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception
	{
		UUID jobID = UUID.fromString("4dddbe5d-1291-46ac-9635-3714160e2e6d"); //UUID.randomUUID();

		JobParameters params = new JobParametersBuilder()
				.addString("JobID", jobID.toString()) // String.valueOf(System.currentTimeMillis())
				.toJobParameters();
		jobLauncher.run(job, params);
	}
}
