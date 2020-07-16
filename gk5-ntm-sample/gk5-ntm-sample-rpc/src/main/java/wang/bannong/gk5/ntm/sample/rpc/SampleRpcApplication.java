package wang.bannong.gk5.ntm.sample.rpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class, scanBasePackages = {"wang.bannong.gk5"})
public class SampleRpcApplication {
    public static void main(String...args) {
        SpringApplication.run(SampleRpcApplication.class, args);
    }
}
