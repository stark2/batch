package com.trxmon.batch.domain;

import java.util.Date;

public class Alert {
    private final long id;
    private final String alert_type;
    private final String alert_description;
    private final Date alert_date;

    public Alert(long id, String alert_type, String alert_description, Date alert_date) {
        this.id = id;
        this.alert_type = alert_type;
        this.alert_description = alert_description;
        this.alert_date = alert_date;
    }

    @Override
    public String toString() {
        return "Alert {" +
                "id=" + id +
                ", type=" + alert_type +
                ", description=" + alert_description +
                ", date=" + alert_date +
                "}";
    }
}