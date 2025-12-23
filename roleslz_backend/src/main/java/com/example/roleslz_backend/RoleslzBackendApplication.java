package com.example.roleslz_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RoleslzBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoleslzBackendApplication.class, args);
	}

}
