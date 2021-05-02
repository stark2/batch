package com.trxmon.batch.domain;

import org.springframework.batch.item.ResourceAware;
import org.springframework.core.io.Resource;

import java.util.Date;

public class Alert implements ResourceAware {
    public long getId() {
        return id;
    }

    public String getAlert_type() {
        return alert_type;
    }

    public String getAlert_description() {
        return alert_description;
    }

    public Date getAlert_date() {
        return alert_date;
    }

    private final long id;
    private final String alert_type;
    private final String alert_description;
    private final Date alert_date;

    private Resource resource;

    public Alert(long id, String alert_type, String alert_description, Date alert_date) {
        this.id = id;
        this.alert_type = alert_type;
        this.alert_description = alert_description;
        this.alert_date = alert_date;
    }

    public Alert(long id, String alert_type, String alert_description, Date alert_date, Resource resource) {
        this.id = id;
        this.alert_type = alert_type;
        this.alert_description = alert_description;
        this.alert_date = alert_date;
        this.resource = resource;
    }

    @Override
    public String toString() {
        return "Alert {" +
                "id=" + id +
                ", type=" + alert_type +
                ", description=" + alert_description +
                ", date=" + alert_date +
                ", resource=" + resource.getFilename() +
                "}";
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Resource getResource() {
        return this.resource;
    }
}