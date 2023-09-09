package br.com.ftgo.gateway.gateway;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import reactor.core.publisher.Mono;

public class LoginAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {
    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
        DataBuffer buffer = webFilterExchange.getExchange().getResponse().bufferFactory().wrap(exception.getMessage().getBytes());

        return webFilterExchange.getExchange().getResponse().writeWith(Mono.just(buffer));
    }
}
