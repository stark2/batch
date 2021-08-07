package com.trxmon.batch.filter;

import com.trxmon.batch.domain.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class AlertItemFilter implements ItemProcessor<Alert, Alert> {
    private static final Logger log = LoggerFactory.getLogger(AlertItemFilter.class);

    @Override
    public Alert process(Alert alert) throws Exception {
        if(alert.getAlert_type().equals("AA")) {
            System.err.println("Filtering " + alert);
            return null;
        } else {
            return alert;
        }
    }
}
