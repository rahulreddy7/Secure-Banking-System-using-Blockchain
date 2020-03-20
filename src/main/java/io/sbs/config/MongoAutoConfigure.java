package io.sbs.config;

import com.mongodb.*;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.util.StringUtils;

/**
 * @date 2020-03-16
 */
@Configuration
@EnableConfigurationProperties({MongoProperties.class})
public class MongoAutoConfigure {

    @Bean
    public MongoClient mongoClient(MongoProperties mongoProperties) {
        String uri = mongoProperties.getUri();
        if (!StringUtils.isEmpty(uri)) {
            return new MongoClient(new MongoClientURI(uri));
        } else {
            String host = mongoProperties.getHost();
            Integer port = mongoProperties.getPort();
            if (StringUtils.isEmpty(host) || port == null) {
                throw new NullPointerException("Please connecte right server address or URI parameters");
            } else {
                MongoCredential credential = MongoCredential.createCredential(mongoProperties.getUsername(),
                        mongoProperties.getAuthenticationDatabase(),
                        mongoProperties.getPassword());
                ServerAddress serverAddress = new ServerAddress(host, port);
                return new MongoClient(serverAddress, credential, getMongoClientOptions(mongoProperties));
            }
        }
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoProperties mongoProperties) {
        SimpleMongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(this.mongoClient(mongoProperties), mongoProperties.getDatabase());
        MappingMongoConverter mongoConverter = new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory), new MongoMappingContext());
        mongoConverter.setTypeMapper(new DefaultMongoTypeMapper((String)null));
        return new MongoTemplate(mongoDbFactory, mongoConverter);
    }

    private MongoClientOptions getMongoClientOptions(MongoProperties mongoProperties) {
        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        return builder.build();
    }
}
