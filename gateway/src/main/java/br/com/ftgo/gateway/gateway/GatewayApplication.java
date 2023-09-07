package br.com.ftgo.gateway.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class GatewayApplication {
	@Value("${gateway.routes.restaurants.url}")
	private String restaurantsUrl;

	@Value("${gateway.routes.customers.url}")
	private String customersUrl;

	@Value("${gateway.routes.orders.url}")
	private String ordersUrl;

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
	public SecurityWebFilterChain securityChain(ServerHttpSecurity http) {
		return http.authorizeExchange(exchange -> exchange.anyExchange().permitAll())
				.csrf(csrf -> csrf.disable()).build();
	}
}
