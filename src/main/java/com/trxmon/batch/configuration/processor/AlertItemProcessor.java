package com.trxmon.batch.configuration.processor;

import com.trxmon.batch.domain.Alert;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.util.Date;
import org.slf4j.Logger;
import org.springframework.core.io.Resource;

public class AlertItemProcessor implements ItemProcessor<Alert, Alert> {

    private static final Logger log = LoggerFactory.getLogger(AlertItemProcessor.class);

    @Override
    public Alert process(Alert alert) throws Exception {
        final long alertId = alert.getId();
        final String alertType =  alert.getAlert_type().replace("_", "").toUpperCase();
        final String alertDescription = alert.getAlert_description().toUpperCase();
        final Date alertDate = alert.getAlert_date();
        final Resource resource = alert.getResource();
        final Alert transformedAlert = new Alert(alertId, alertType, alertDescription, alertDate, resource);

        log.info("Converting (" + alert + ") into (" + transformedAlert + ")");
        return transformedAlert;
    }
}
