package com.samcomo.dbz.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = {
    "com.samcomo.dbz.chat.model.repository",
    "com.samcomo.dbz.notification.model.repository"
})
public class MongoConfig {
}
