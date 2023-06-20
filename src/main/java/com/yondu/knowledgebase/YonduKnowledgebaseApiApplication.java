package com.yondu.knowledgebase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class YonduKnowledgebaseApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(YonduKnowledgebaseApiApplication.class, args);
	}

}
