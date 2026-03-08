package com.banafrance.annuaire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AnnuaireBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnnuaireBackendApplication.class, args);
	}

}
