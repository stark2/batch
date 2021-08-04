package com.trxmon.batch.configuration;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ParameterValidator implements JobParametersValidator {

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String alert_date = parameters.getString("alert_date");
        SimpleDateFormat alert_date_format = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            Date javaDate = alert_date_format.parse(alert_date);
        }
        catch (ParseException e)
        {
            throw new JobParametersInvalidException(alert_date + " has invalid date format");
        }
    }
}
