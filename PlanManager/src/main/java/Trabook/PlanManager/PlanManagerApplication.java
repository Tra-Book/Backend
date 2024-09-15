package Trabook.PlanManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
//@EnableDiscoveryClient
public class PlanManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlanManagerApplication.class, args);
	}

}

@RestController
class HelloWorld {
	@GetMapping("/")
	public String greet() {
		return "hello";
	}
}
