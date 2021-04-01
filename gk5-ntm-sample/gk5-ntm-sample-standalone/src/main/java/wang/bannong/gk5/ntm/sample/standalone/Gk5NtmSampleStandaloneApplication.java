package wang.bannong.gk5.ntm.sample.standalone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}, scanBasePackages = {"wang.bannong"})
public class Gk5NtmSampleStandaloneApplication {
    public static void main(String[] args) {
        SpringApplication.run(Gk5NtmSampleStandaloneApplication.class, args);
    }
}
