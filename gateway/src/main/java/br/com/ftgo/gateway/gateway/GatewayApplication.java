package br.com.ftgo.gateway.gateway;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@SpringBootApplication
@EnableWebFluxSecurity
@Configuration
public class GatewayApplication {
	@Value("${gateway.routes.restaurants.url}")
	private String restaurantsUrl;

	@Value("${gateway.routes.customers.url}")
	private String customersUrl;

	@Value("${gateway.routes.orders.url}")
	private String ordersUrl;

	@Value("${app.security.publicKey}")
	private RSAPublicKey publicKey;

	@Value("${app.security.privateKey}")
	private RSAPrivateKey privateKey;

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public RouteLocator routeLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(spec -> spec.path("/restaurants/**").uri(restaurantsUrl))
				.route(spec -> spec.path("/customers/**").uri(customersUrl))
				.route(spec -> spec.path("/orders/**").uri(ordersUrl))
				.build();
	}

	@Bean
	public ReactiveJwtDecoder jwtDecoder() {
		return NimbusReactiveJwtDecoder.withPublicKey(publicKey).build();
	}

	@Bean
	public JwtEncoder jwtEncoder() {
		JWK key = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
		JWKSource<SecurityContext> source = new ImmutableJWKSet<>(new JWKSet(key));

		return new NimbusJwtEncoder(source);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public MapReactiveUserDetailsService userDetailsService() {
		UserDetails user = User
				.withUsername("user")
				.password(passwordEncoder().encode("password"))
				.roles("USER")
				.build();

		UserDetails admin = User
				.withUsername("admin")
				.password(passwordEncoder().encode("password"))
				.roles("ADMIN")
				.build();

		return new MapReactiveUserDetailsService(user, admin);
	}

	@Bean
	public SecurityWebFilterChain securityChain(ServerHttpSecurity http) {
		return http.authorizeExchange(exchange -> exchange.anyExchange().authenticated())
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
				.formLogin(login -> login
						.authenticationSuccessHandler(new LoginAuthenticationSuccessHandler(jwtEncoder()))
						.authenticationFailureHandler(new LoginAuthenticationFailureHandler())
						.securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
				)
				.httpBasic(basic -> basic.disable())
				.csrf(csrf -> csrf.disable())
				.build();
	}
}
