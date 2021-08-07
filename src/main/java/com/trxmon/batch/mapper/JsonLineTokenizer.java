package com.trxmon.batch.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonLineTokenizer implements LineTokenizer {

    @Override
    public FieldSet tokenize(String line) {
        List<String> tokens = new ArrayList<>();

        try {
            HashMap<String,Object> result = new ObjectMapper().readValue(line, HashMap.class);

            tokens.add(((Integer)result.get("alert_id")).toString());
            tokens.add((String) result.get("alert_type"));
            tokens.add((String) result.get("alert_description"));
            tokens.add((String) result.get("alert_date"));


        } catch (IOException e) {
            throw new RuntimeException("Unable to parse json: " + line);
        }

        return new DefaultFieldSet(tokens.toArray(new String[0]),
                new String[]{"alert_id", "alert_type", "alert_description", "alert_date"});
    }
}