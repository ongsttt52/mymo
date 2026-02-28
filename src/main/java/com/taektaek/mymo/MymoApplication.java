package com.taektaek.mymo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MymoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MymoApplication.class, args);
	}

}
