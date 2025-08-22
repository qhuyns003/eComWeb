package com.qhuyns.ecomweb;

import co.elastic.apm.attach.ElasticApmAttacher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EcomwebApplication {

	public static void main(String[] args) {
		ElasticApmAttacher.attach();
		SpringApplication.run(EcomwebApplication.class, args);
	}

}
