package com.qhuyns.ecomweb;

import co.elastic.apm.attach.ElasticApmAttacher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

// spring mvc: giup confgi servlet
// springboot : giup config webserver
// -> nam trong spring boot starter
// spring dât jpa : config jdbc
@SpringBootApplication
@EnableFeignClients
public class EcomwebApplication {

	public static void main(String[] args) {
		ElasticApmAttacher.attach();
		SpringApplication.run(EcomwebApplication.class, args);
	}

}
