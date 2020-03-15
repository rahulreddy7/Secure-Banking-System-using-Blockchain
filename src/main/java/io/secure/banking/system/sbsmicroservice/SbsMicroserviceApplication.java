package io.secure.banking.system.sbsmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.SpringVersion;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@ComponentScan({"io.sbs.*"})
@EnableMongoRepositories("io.sbs.*")
@EnableAutoConfiguration()
public class SbsMicroserviceApplication {

	public static void main(String[] args) {
		System.out.println("version: " + SpringVersion.getVersion());
		SpringApplication.run(SbsMicroserviceApplication.class, args);
	}

}
