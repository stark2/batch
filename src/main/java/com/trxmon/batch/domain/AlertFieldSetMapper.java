package com.trxmon.batch.domain;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class AlertFieldSetMapper implements FieldSetMapper<Alert> {

    @Override
    public Alert mapFieldSet(FieldSet fieldSet) {
        return new Alert(fieldSet.readLong("alert_id"),
                fieldSet.readString("alert_type"),
                fieldSet.readString("alert_description"),
                fieldSet.readDate("alert_date", "yyyy-MM-dd HH:mm:ss"));
    }
}