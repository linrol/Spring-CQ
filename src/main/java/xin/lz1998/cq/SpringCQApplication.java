package xin.lz1998.cq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringCQApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCQApplication.class, args);
    }
}
