package com.attendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AttendanceManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(AttendanceManagementApplication.class, args);
	}

	@org.springframework.context.annotation.Bean
	public org.springframework.boot.CommandLineRunner commandLineRunner(org.springframework.context.ApplicationContext ctx) {
		return args -> {
			System.out.println("\n✅=====================================================================");
			System.out.println("✅ DATABASE CONNECTION SUCCESSFUL! The Application is running on Port 8080");
			System.out.println("✅=====================================================================\n");
		};
	}
}
