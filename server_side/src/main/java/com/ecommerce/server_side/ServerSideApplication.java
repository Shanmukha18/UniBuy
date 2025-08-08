package com.ecommerce.server_side;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class ServerSideApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerSideApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void debugEnvironmentVariables() {
		System.out.println("=== Environment Variables Debug ===");
		System.out.println("DATABASE_URL: " + System.getenv("DATABASE_URL"));
		System.out.println("DATABASE_USERNAME: " + System.getenv("DATABASE_USERNAME"));
		System.out.println("DATABASE_PASSWORD: " + (System.getenv("DATABASE_PASSWORD") != null ? "***SET***" : "NOT SET"));
		System.out.println("JWT_SECRET: " + (System.getenv("JWT_SECRET") != null ? "***SET***" : "NOT SET"));
		System.out.println("ALLOWED_ORIGINS: " + System.getenv("ALLOWED_ORIGINS"));
		System.out.println("==================================");
	}
}
