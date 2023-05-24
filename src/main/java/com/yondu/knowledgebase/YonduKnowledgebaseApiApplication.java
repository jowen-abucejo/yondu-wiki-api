package com.yondu.knowledgebase;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class YonduKnowledgebaseApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(YonduKnowledgebaseApiApplication.class, args);
	}

}
