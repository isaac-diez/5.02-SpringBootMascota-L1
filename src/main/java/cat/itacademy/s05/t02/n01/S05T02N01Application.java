package cat.itacademy.s05.t02.n01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class S05T02N01Application {

	public static void main(String[] args) {
		SpringApplication.run(S05T02N01Application.class, args);
	}

}
