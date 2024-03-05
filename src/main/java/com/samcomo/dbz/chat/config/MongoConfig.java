package com.samcomo.dbz.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.samcomo.dbz.chat.model.repository")
public class MongoConfig {
}
