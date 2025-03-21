package id.co.bcaf.adapinjam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EntityScan(basePackages = "id.co.bcaf.adapinjam.models")
@ComponentScan(basePackages = "id.co.bcaf.adapinjam")
@EnableJpaRepositories(basePackages = "id.co.bcaf.adapinjam.repositories")
public class AdapinjamApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdapinjamApplication.class, args);
	}

}
