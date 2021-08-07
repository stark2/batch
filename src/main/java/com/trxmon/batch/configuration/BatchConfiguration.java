package com.trxmon.batch.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.batch.BatchDataSourceInitializer;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
public class BatchConfiguration extends DefaultBatchConfigurer {
    private static final Log log = LogFactory.getLog(BatchConfiguration.class);

    @Value("${datasource.batch.table-prefix}")
    private String table_prefix;

    private DataSource dataSource;

    @Override
    @Autowired
    public void setDataSource(@Qualifier("batchDataSource") DataSource batchDataSource) {
        super.setDataSource(batchDataSource);
        this.dataSource = batchDataSource;
    }

    @Bean
    public BatchDataSourceInitializer batchDataSourceInitializer(
            @Qualifier("batchDataSource") DataSource batchDataSource,
            ResourceLoader resourceLoader) {
        return new BatchDataSourceInitializer(batchDataSource, resourceLoader, new BatchProperties());
    }

    @Override
    protected JobRepository createJobRepository() throws Exception {
        JobRepositoryFactoryBean factoryBean = new JobRepositoryFactoryBean();
        factoryBean.setTablePrefix(table_prefix);  // all tables have to be created manually
        factoryBean.setTransactionManager(this.getTransactionManager());
        factoryBean.setDataSource(this.dataSource);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

}
