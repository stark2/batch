package com.trxmon.batch.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {
    @Bean
    @Primary
    @ConfigurationProperties("datasource.primary")
    public DataSource domainDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("batchDataSource")
    @ConfigurationProperties("datasource.batch")
    public DataSource batchDataSource() {
        return DataSourceBuilder.create().build();
    }
}
