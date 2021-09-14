package com.org.somak.demo.nosql.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
//@EnableMongoRepositories(
//basePackages = {"com.yyy.dao.jpa", "com.xxx.dao.jpa"},
//mongoTemplateRef = "MovieTemplate"
//)
public class MongoConfiguration {

	@Bean
	public MongoTemplate mongoTemplate(@Value("${data.name}") String dataBaseName,
			@Value("${data.username}") String userName, @Value("${data.password}") String password) throws Exception {
		MongoCredential credential = MongoCredential.createCredential(userName, dataBaseName, password.toCharArray());
		return new CustomMongoTemplate(mongo(), dataBaseName);
	}

	@Bean
	public MongoClient mongo() {
		return MongoClients.create();
	}

	 @Bean
	 public MyMongoEventListener getMyListener() {
	     return new MyMongoEventListener();
	 }

}
