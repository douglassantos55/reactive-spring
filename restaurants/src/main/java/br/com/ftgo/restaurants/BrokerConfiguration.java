package br.com.ftgo.restaurants;

import org.springframework.amqp.core.TopicExchange;
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
    public CachingConnectionFactoryConfigurer factoryConfigurer() {
        RabbitProperties properties = new RabbitProperties();

        properties.setHost(hostname);
        properties.setUsername(username);
        properties.setPassword(password);
        properties.setPort(port);

        return new CachingConnectionFactoryConfigurer(properties);
    }

    @Bean
    public TopicExchange restaurantsExchange() {
        return new TopicExchange("notifications.exchange", true, false);
    }
}
