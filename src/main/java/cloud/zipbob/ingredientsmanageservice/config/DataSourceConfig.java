package cloud.zipbob.ingredientsmanageservice.config;

import cloud.zipbob.ingredientsmanageservice.global.datasource.RoutingDataSource;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
@EnableTransactionManagement
@RequiredArgsConstructor
public class DataSourceConfig {

    private final MariaDbProperties mariaDbProperties;

    @Bean(name = "masterDataSource")
    public DataSource masterDataSource() {
        log.info("Initializing Master DataSource");
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(mariaDbProperties.getMaster().getUrl());
        dataSource.setDriverClassName(mariaDbProperties.getMaster().getDriverClassName());
        dataSource.setUsername(mariaDbProperties.getMaster().getUsername());
        dataSource.setPassword(mariaDbProperties.getMaster().getPassword());
        return dataSource;
    }

    @Bean(name = "slaveDataSource")
    public DataSource slaveDataSource() {
        log.info("Initializing Slave DataSource");
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(mariaDbProperties.getSlave().getUrl());
        dataSource.setDriverClassName(mariaDbProperties.getSlave().getDriverClassName());
        dataSource.setUsername(mariaDbProperties.getSlave().getUsername());
        dataSource.setPassword(mariaDbProperties.getSlave().getPassword());
        return dataSource;
    }

    @Bean(name = "routingDataSource")
    @Primary
    public DataSource dataSource() {
        log.info("Initializing Routing DataSource");
        RoutingDataSource routingDataSource = new RoutingDataSource();

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("master", masterDataSource());
        targetDataSources.put("slave", slaveDataSource());

        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(masterDataSource());

        return routingDataSource;
    }
}
