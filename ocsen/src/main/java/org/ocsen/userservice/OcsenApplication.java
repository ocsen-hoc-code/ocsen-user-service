package org.ocsen.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@ServletComponentScan
@SpringBootApplication
public class OcsenApplication {

	public static void main(String[] args) {
		SpringApplication.run(OcsenApplication.class, args);
	}

}
