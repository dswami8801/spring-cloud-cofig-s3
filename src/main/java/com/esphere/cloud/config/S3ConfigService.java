package com.esphere.cloud.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class S3ConfigService {
	public static void main(String[] args) {
		SpringApplication.run(S3ConfigService.class, args);
	}
}
