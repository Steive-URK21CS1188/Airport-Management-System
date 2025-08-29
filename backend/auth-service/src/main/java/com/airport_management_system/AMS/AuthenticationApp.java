package com.airport_management_system.AMS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class AuthenticationApp{

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationApp.class, args);
	}
	

}
