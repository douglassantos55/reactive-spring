package br.com.reconcip.customers;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfiguration {
    @Value("${amqp.hostname}")
    private String hostname;

    @Value("${amqp.username}")
    private String username;

    @Value("${amqp.password}")
    private String password;

    @Value("${amqp.port}")
    private int port;

    @Bean
    public Exchange customersExchange() {
        return new TopicExchange("exchange.customers");
    }

    @Bean
    public ConnectionFactory cachingConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory(hostname, port);

        factory.setUsername(username);
        factory.setPassword(password);

        return factory;
    }
}
