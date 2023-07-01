package br.com.fgto.customers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Controller
@SpringBootApplication
public class CustomersApplication {
	public static void main(String[] args) {
		SpringApplication.run(CustomersApplication.class, args);
	}

	@GetMapping("/greet")
	@ResponseBody
	public Greeting greet() {
		return new Greeting("Hello from reactive");
	}

	public Mono<ServerResponse> hello() {
		return ServerResponse
				.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(new Greeting("Hello from reactive")));
	}

	@Bean
	public RouterFunction<ServerResponse> router() {
		return RouterFunctions.route(RequestPredicates.GET("/hello"), request -> this.hello());
	}
}

class Greeting {
	public String message;

	public Greeting() {}

	public Greeting(String message) {
		this.message = message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public String toString() {
		return message;
	}
}