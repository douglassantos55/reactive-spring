package br.com.ftgo.restaurants;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import reactor.core.publisher.Hooks;

@SpringBootApplication
@EnableScheduling
@EnableR2dbcAuditing
@EnableReactiveMongoAuditing
public class RestaurantsApplication {

	public static void main(String[] args) {
		// Secret sauce for context propagation to work without much hassle with reactivity
		Hooks.enableAutomaticContextPropagation();

		SpringApplication.run(RestaurantsApplication.class, args);
	}

}
