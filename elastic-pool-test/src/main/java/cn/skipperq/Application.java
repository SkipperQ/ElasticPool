package cn.skipperq;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@Configurable
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public ApplicationRunner applicationRunner(ExecutorService threadPoolExecutor01) {
        return args -> {
            while (true){
                Random random = new Random();
                int initialDelay = random.nextInt(10) + 1;
                int sleepTime = random.nextInt(10) + 1;

                threadPoolExecutor01.submit(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(initialDelay);
                        System.out.println("Task started after " + initialDelay + " seconds.");

                        TimeUnit.SECONDS.sleep(sleepTime);
                        System.out.println("Task executed for " + sleepTime + " seconds.");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });

                Thread.sleep(random.nextInt(50) + 1);
            }
        };
    }
}
