package cn.skipperq.middleware.elastic.pool;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@SpringBootApplication
@Configurable
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Configuration
    @EnableConfigurationProperties(RedisClientConfigProperties.class)
    public static class RedisClientConfig {

        @Bean("redissonClient")
        public RedissonClient redissonClient(ConfigurableApplicationContext applicationContext, RedisClientConfigProperties properties) {
            Config config = new Config();

            config.setCodec(JsonJacksonCodec.INSTANCE);

            config.useSingleServer()
                    .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
                    .setConnectionPoolSize(properties.getPoolSize())
                    .setConnectionMinimumIdleSize(properties.getMinIdleSize())
                    .setIdleConnectionTimeout(properties.getIdleTimeout())
                    .setConnectTimeout(properties.getConnectTimeout())
                    .setRetryAttempts(properties.getRetryAttempts())
                    .setRetryInterval(properties.getRetryInterval())
                    .setPingConnectionInterval(properties.getPingInterval())
                    .setKeepAlive(properties.isKeepAlive())
            ;

            return Redisson.create(config);
        }

    }


    @Data
    @ConfigurationProperties(prefix = "redis.sdk.config", ignoreInvalidFields = true)
    @Slf4j
    public static class RedisClientConfigProperties {

        private String host;

        private int port;

        private String password;

        private int poolSize = 64;

        private int minIdleSize = 10;

        private int idleTimeout = 10000;

        private int connectTimeout = 10000;

        private int retryAttempts = 3;

        private int retryInterval = 1000;

        private int pingInterval = 0;

        private boolean keepAlive = true;
    }

}
