//  
//package io.sbs.model;
//
//import java.time.Duration;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.session.data.mongo.JacksonMongoSessionConverter;
//import org.springframework.session.data.mongo.JdkMongoSessionConverter;
//import org.springframework.session.data.mongo.MongoIndexedSessionRepository;
//import org.springframework.session.data.mongo.config.annotation.web.http.EnableMongoHttpSession;
//
//
//@EnableMongoHttpSession
//public class HttpSessionConfig {
////	@Bean
////	public JdkMongoSessionConverter jdkMongoSessionConverter() {
////		return new JdkMongoSessionConverter(Duration.ofSeconds(900));
////	}
//	@Bean
//	public JacksonMongoSessionConverter sessionConverter() {
//		return new JacksonMongoSessionConverter();
//	}
//}