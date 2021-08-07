package com.trxmon.batch.validator;

import com.trxmon.batch.domain.Alert;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

public class AlertItemValidator implements Validator<Alert> {
    @Override
    public void validate(Alert alert) throws ValidationException {
        if(!alert.getAlert_type().equals("XX")) {
            throw new ValidationException("Wrong alert type: " + alert);
        }
    }
}
