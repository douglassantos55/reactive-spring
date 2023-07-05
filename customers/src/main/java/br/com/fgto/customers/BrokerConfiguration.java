package br.com.fgto.customers;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.CachingConnectionFactoryConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BrokerConfiguration {
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
        return new TopicExchange("notifications.exchange", true, false);
    }

    @Bean
    public CachingConnectionFactoryConfigurer connectionFactoryConfigurer() {
        RabbitProperties properties = new RabbitProperties();

        properties.setHost(hostname);
        properties.setPort(port);
        properties.setUsername(username);
        properties.setPassword(password);

        return new CachingConnectionFactoryConfigurer(properties);
    }
}
