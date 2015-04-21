package com.leidos.xchangecore.adapter.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.oxm.xmlbeans.XmlBeansMarshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

import com.leidos.xchangecore.adapter.XchangeCoreAdapter;
import com.leidos.xchangecore.adapter.dao.CsvConfigurationDao;
import com.leidos.xchangecore.adapter.dao.MappedRecordDao;

@Configuration
public class AdapterSpringConfig {

    @Value("${jdbc.url}")
    private String jdbcUrl;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Value("${hibernate.persistence.package}")
    private String persistencePackage;

    private static final String Key_hibernate_hbm2dll_auto = "hibernate.hbm2ddl.auto";
    @Value("${hibernate.hbm2dll.auto}")
    private String hbm2dll;

    @Value("${hibernate.showSql}")
    private String showSql;

    @Value("${hibernate.generateDdl}")
    private String generateDdl;

    @Value("${hibernate.databasePlatform}")
    private String databasePlatform;

    @Bean
    public CsvConfigurationDao csvConfigurationDao() {

        return new CsvConfigurationDao();
    }

    @Bean
    public DataSource dataSource() {

        return new SimpleDriverDataSource(new org.hsqldb.jdbc.JDBCDriver(),
                                          jdbcUrl,
                                          username,
                                          password);
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {

        final LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();
        emfBean.setDataSource(dataSource());
        emfBean.setPersistenceProvider(new HibernatePersistenceProvider());
        emfBean.setPackagesToScan(persistencePackage);
        emfBean.setJpaProperties(jpaProperties());
        emfBean.setJpaVendorAdapter(jpaVendorAdapter());

        emfBean.afterPropertiesSet();

        return emfBean.getObject();
    }

    @Bean
    public Properties jpaProperties() {

        final Properties properties = new Properties();
        properties.setProperty(Key_hibernate_hbm2dll_auto, hbm2dll);

        return properties;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {

        final HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(showSql.equalsIgnoreCase("true") ? true : false);
        adapter.setDatabasePlatform(databasePlatform);
        adapter.setGenerateDdl(generateDdl.equalsIgnoreCase("true") ? true : false);

        return adapter;
    }

    @Bean
    public MappedRecordDao mappedRecordDao() {

        return new MappedRecordDao();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {

        return new JpaTransactionManager(entityManagerFactory());
    }

    @Bean
    public WebServiceTemplate webServiceTemplate() throws SOAPException {

        // need to pass MessageFactory.newInstance to create a SaajSoapMessageFactory
        final WebServiceTemplate ws = new WebServiceTemplate(new SaajSoapMessageFactory(MessageFactory.newInstance()));

        ws.setMarshaller(new XmlBeansMarshaller());
        ws.setUnmarshaller(new XmlBeansMarshaller());

        return ws;
    }

    @Bean
    public XchangeCoreAdapter xchangeCoreAdapter() {

        return new XchangeCoreAdapter();
    }
}
