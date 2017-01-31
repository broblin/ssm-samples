package demo.orderservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;

/**
 * Created by montassar on 30/01/17.
 */

@Configuration
public class DataSourceConfig {


    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfig.class);

    @Autowired
    Environment env;

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }


    @Bean
    @Lazy(false)
    @Qualifier("stateDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.state")
    public DataSource pivotDataSource() {
        LOGGER.info("Connecting to database pivot (postgres) on : {}", this.env.getProperty("spring.datasource.state.url"));
        return DataSourceBuilder.create().build();
    }



}
