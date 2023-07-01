package br.com.fgto.customers;

import com.rabbitmq.client.Channel;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.io.IOException;

@TestConfiguration
public class AmqpTestConfiguration {
    @Bean
    @Primary
    public CachingConnectionFactory testConnectionFactory() throws IOException {
        CachingConnectionFactory factory = Mockito.mock(CachingConnectionFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        Channel channel = Mockito.mock(Channel.class);

        Mockito.doReturn(connection).when(factory).createConnection();
        Mockito.doReturn(channel).when(connection).createChannel(Mockito.anyBoolean());
        Mockito.when(channel.isOpen()).thenReturn(true);

        return factory;
    }
}
