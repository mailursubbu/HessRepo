package com.cspire.prov.dtf.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableJpaRepositories( basePackages="com.cspire.prov.dtf.model",
                        entityManagerFactoryRef="dtfEntityManagerFactory",
                        transactionManagerRef="dtfTransactionManager")
@EnableTransactionManagement 
class DtfDataBaseConfig {
	
	Logger log = Logger.getLogger(DtfDataBaseConfig.class);
	@Bean
	@Primary
	@ConfigurationProperties(prefix="cspire.dtf.datasource")
	public DataSource houseKeepingDataSource() {
		return DataSourceBuilder
				.create()
				.build();
	}
	
	@Primary
	@Bean(name = "dtfEntityManagerFactory")
	public EntityManagerFactory dtfEntityManagerFactory() {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(jpaVendorAdapter());
		factory.setPackagesToScan("com.cspire.prov.dtf.model");	
		factory.setDataSource(houseKeepingDataSource());
		factory.setPersistenceUnitName("dtfPersistenceUnit");
		factory.afterPropertiesSet();
		return factory.getObject();
	}
	
	@Primary
	@Bean(name = "dtfTransactionManager")
	public PlatformTransactionManager dtfTransactionManager() {
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(dtfEntityManagerFactory());
		return txManager;
	}
		
	private JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setShowSql(false);
        jpaVendorAdapter.setGenerateDdl(false);
        //jpaVendorAdapter.setDatabase(Database.POSTGRESQL);
        jpaVendorAdapter.setDatabase(Database.SQL_SERVER);
        return jpaVendorAdapter;
    }
}
