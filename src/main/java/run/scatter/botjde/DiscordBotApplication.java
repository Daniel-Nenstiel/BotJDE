package run.scatter.botjde;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import run.scatter.botjde.config.AppConfig;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "run.scatter.botjde")
@EnableConfigurationProperties(AppConfig.class)
public class DiscordBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscordBotApplication.class, args);
	}
}
