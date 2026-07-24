package com.company.hrsettlement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entree de l'application.
 * <p>
 * Seul l'adaptateur primaire (Spring Boot) connait le framework : le domaine reste
 * totalement independant de Spring.
 */
@SpringBootApplication
public class HrSettlementApplication {

	public static void main(String[] args) {
		SpringApplication.run(HrSettlementApplication.class, args);
	}
}
