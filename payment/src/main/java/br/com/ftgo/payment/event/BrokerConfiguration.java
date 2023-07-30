package br.com.ftgo.payment.event;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.CachingConnectionFactoryConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BrokerConfiguration {
    @Value("${amqp.hostname}")
    private String host;

    @Value("${amqp.username")
    private String username;

    @Value("${amqp.password}")
    private String password;

    @Value("${amqp.port}")
    private int port;

    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange("payment.exchange", true, false);
    }

    @Bean
    public CachingConnectionFactoryConfigurer configurer() {
        RabbitProperties props = new RabbitProperties();

        props.setHost(host);
        props.setUsername(username);
        props.setPassword(password);
        props.setPort(port);

        return new CachingConnectionFactoryConfigurer(props);
    }
}