package ca.reppy.reppy_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class ReppyBackendApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(ReppyBackendApiApplication.class, args);
	}
}
