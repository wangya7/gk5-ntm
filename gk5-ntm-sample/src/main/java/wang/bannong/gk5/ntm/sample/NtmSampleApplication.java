package wang.bannong.gk5.ntm.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class, scanBasePackages = {"wang.bannong"})
public class NtmSampleApplication {
    public static void main(String... args) {
        SpringApplication.run(NtmSampleApplication.class, args);
    }
}
