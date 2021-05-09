package com.trxmon.batch;

import org.apache.commons.cli.*;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.apache.commons.io.FileUtils;

@SpringBootApplication
@EnableBatchProcessing
public class BatchApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(BatchApplication.class);

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
		Options options = new Options();

		Option input = new Option("p", "path", true, "path to alert files");
		input.setRequired(true);
		options.addOption(input);

		Option output = new Option("d", "date", true, "alert date");
		output.setRequired(true);
		options.addOption(output);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();

		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp(this.getClass().getName(), options);
			System.exit(1);
		}

		String alert_path = cmd.getOptionValue("path");
		String alert_date = cmd.getOptionValue("date");

		if (alert_path != null && alert_path.length() > 0
				&& alert_path.charAt(alert_path.length() - 1) == '/') {
			alert_path = alert_path.substring(0, alert_path.length() - 1);
		}

		Path path = Path.of(alert_path);
		if (!Files.isDirectory(path)) {
			log.info("Not a valid path: " + alert_path);
			System.exit(1);
		}

		File dir = new File(path.toString());
		FileFilter fileFilter = new WildcardFileFilter("*" + alert_date + "*.json");
		File[] files = dir.listFiles(fileFilter);

		if (files.length <= 0) {
			log.info("No files found in " + path.toString()
					+ " with pattern: " + "*" + alert_date + "*.json");
			System.exit(1);
		}

		for (int i = 0; i < files.length; i++) {
			log.info("File to be processed: " + files[i].getName());
		}

		log.info("Executing the command line runner, application arguments: "
				+ Arrays.toString(args));

		//UUID jobID = UUID.randomUUID();

		JobParameters params = new JobParametersBuilder()
				.addString("alert_path", alert_path)
				.addString("alert_date", alert_date)
				.toJobParameters();
		jobLauncher.run(job, params);
	}
}
