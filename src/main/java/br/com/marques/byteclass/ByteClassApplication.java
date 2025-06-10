package br.com.marques.byteclass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ByteClassApplication {

	public static void main(String[] args) {
		SpringApplication.run(ByteClassApplication.class, args);
	}

}
