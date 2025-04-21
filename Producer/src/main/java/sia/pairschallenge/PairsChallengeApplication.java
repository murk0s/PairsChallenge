package sia.pairschallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = "sia")
@EnableCaching
public class PairsChallengeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PairsChallengeApplication.class, args);
    }

}
