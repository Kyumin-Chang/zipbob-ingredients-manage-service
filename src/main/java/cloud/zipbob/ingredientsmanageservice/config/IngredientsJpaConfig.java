package cloud.zipbob.ingredientsmanageservice.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "cloud.zipbob.ingredientsmanageservice.domain.ingredient.repository",
        entityManagerFactoryRef = "ingredientsEntityManagerFactory",
        transactionManagerRef = "ingredientsTransactionManager"
)
public class IngredientsJpaConfig {

    @Bean(name = "ingredientsEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean ingredientsEntityManagerFactory(
            @Qualifier("routingDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPackagesToScan("cloud.zipbob.ingredientsmanageservice.domain.ingredient");
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factory.setJpaPropertyMap(hibernateProperties());
        return factory;
    }

    @Bean(name = "ingredientsTransactionManager")
    public PlatformTransactionManager ingredientsTransactionManager(
            @Qualifier("ingredientsEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    private Map<String, Object> hibernateProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.format_sql", true);
        properties.put("hibernate.use_sql_comments", true);
        properties.put("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect");
        return properties;
    }
}
