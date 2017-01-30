package demo.eventservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Configuration(value = "JpaConf")
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "pivotEntityManagerFactory",
        transactionManagerRef = "pivotTransactionManager",
        value = "demo.eventservice.repositories"
)
public class JpaEntityManagerConfig {
    @Autowired
    JpaVendorAdapter jpaVendorAdapter;

    @Autowired
    @Qualifier("stateDataSource")
    DataSource datasource;

    @Autowired
    private Environment env;

    @SuppressWarnings("serial")
    Properties additionalJpaProperties() {
        List<String> propertyList = new ArrayList<String>() {
            {
                add("hibernate.show_sql");
                add("hibernate.format_sql");
                add("hibernate.ejb.naming_strategy");
                add("hibernate.order_inserts");
                add("hibernate.order_updates");
                add("hibernate.jdbc.batch_size");
                add("hibernate.cache.use_second_level_cache");
                add("hibernate.hbm2ddl.auto");
                add("hibernate.dialect");
            }
        };

        Properties properties = new Properties();
        propertyList.forEach((String propertyName) -> {
            String propertyValue = env.getProperty(propertyName);
            if (propertyValue != null) {
                properties.put(propertyName, propertyValue);
            }
        });
        return properties;
    }

    @Bean(name = "pivotEntityManagerFactory")
    public EntityManagerFactory entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
        lef.setJpaProperties(additionalJpaProperties());
        lef.setDataSource(this.datasource);
        lef.setJpaVendorAdapter(this.jpaVendorAdapter);
        lef.setPackagesToScan("demo.eventservice.entities");
        lef.setPersistenceUnitName("pivotPersistenceUnit");
        lef.afterPropertiesSet();
        return lef.getObject();
    }

    @Bean(name = "pivotTransactionManager")
    public PlatformTransactionManager pivotTransactionManager(
            @Qualifier("pivotEntityManagerFactory") EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name = "pivotEntityManager")
    public EntityManager entityManager(
            @Qualifier("pivotEntityManagerFactory") EntityManagerFactory entityManagerFactory
    ) {
        return entityManagerFactory.createEntityManager();
    }




}
