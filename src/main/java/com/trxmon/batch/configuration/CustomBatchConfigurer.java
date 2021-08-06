package com.trxmon.batch.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class CustomBatchConfigurer extends DefaultBatchConfigurer {
    private static final Log log = LogFactory.getLog(CustomBatchConfigurer.class);

    @Autowired
    private DataSource dataSource;

    @Value("${spring.batch.table-prefix}")
    private String table_prefix;

    @Override
    protected JobRepository createJobRepository() throws Exception {
        JobRepositoryFactoryBean factoryBean = new JobRepositoryFactoryBean();
        factoryBean.setTablePrefix(table_prefix);  // all tables have to be created manually
        factoryBean.setDataSource(this.dataSource);
        factoryBean.setTransactionManager(this.getTransactionManager());
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }
}
