package at.saekenz.cinerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@SpringBootApplication()
public class CineratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CineratorApplication.class, args);
    }

    @GetMapping("/greet")
    public String greet(@RequestParam(value = "myName", defaultValue = "world") String name) {
        return String.format("Hello %s!", name);
    }

}
