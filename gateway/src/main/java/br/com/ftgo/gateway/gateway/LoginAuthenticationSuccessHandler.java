package br.com.ftgo.gateway.gateway;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import reactor.core.publisher.Mono;

import java.time.Instant;

public class LoginAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {
    private JwtEncoder encoder;

    public LoginAuthenticationSuccessHandler(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        DataBuffer buffer = webFilterExchange.getExchange().getResponse().bufferFactory().wrap(getToken(user.getUsername()).getBytes());

        return webFilterExchange.getExchange().getResponse().writeWith(Mono.just(buffer));
    }

    private String getToken(String username) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(username)
                .issuer("br.com.ftgo.gateway.security")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .build();

        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
