package br.com.fgto.customers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;

@Controller
@SpringBootApplication
public class CustomersApplication {
	public static void main(String[] args) {
		SpringApplication.run(CustomersApplication.class, args);
	}
}