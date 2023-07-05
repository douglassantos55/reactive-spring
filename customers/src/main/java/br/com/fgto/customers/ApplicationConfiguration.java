package br.com.fgto.customers;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableR2dbcAuditing
public class ApplicationConfiguration {
    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory factory) {
        String filename = String.format("schema-%s.sql", factory.getMetadata().getName().toLowerCase());
        ClassPathResource schema = new ClassPathResource(filename);

        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(factory);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(schema));

        return initializer;
    }
}
