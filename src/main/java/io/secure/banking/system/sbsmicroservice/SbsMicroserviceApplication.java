package io.secure.banking.system.sbsmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@ComponentScan({"io.sbs.*"})
@EnableMongoRepositories("io.sbs.*")
@EnableAutoConfiguration()
public class SbsMicroserviceApplication{
	
	public static void main(String[] args) {
		SpringApplication.run(SbsMicroserviceApplication.class, args);
	}
	

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
