package cn.skipperq.middleware.elastic.pool.sdk.config;

import cn.skipperq.middleware.elastic.pool.sdk.domain.ElasticPoolService;
import cn.skipperq.middleware.elastic.pool.sdk.domain.IElasticPoolService;
import cn.skipperq.middleware.elastic.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import cn.skipperq.middleware.elastic.pool.sdk.domain.model.valobj.RegistryEnumVO;
import cn.skipperq.middleware.elastic.pool.sdk.registry.IRegistry;
import cn.skipperq.middleware.elastic.pool.sdk.registry.redis.RedisRegistry;
import cn.skipperq.middleware.elastic.pool.sdk.trigger.job.ThreadPoolDataReportJob;
import cn.skipperq.middleware.elastic.pool.sdk.trigger.listener.ThreadPoolConfigAdjustListener;
import org.apache.commons.lang.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/*
    Configuration Entry
 */
@Configuration
@EnableConfigurationProperties(ElasticPoolAutoProperties.class)
@EnableScheduling
public class ElasticPoolAutoConfig {

    private final Logger logger = LoggerFactory.getLogger(ElasticPoolAutoConfig.class);

    private String applicationName;

    @Bean("redissonClient")
    public RedissonClient redissonClient(ElasticPoolAutoProperties properties) {
        Config config = new Config();
        config.setCodec(JsonJacksonCodec.INSTANCE);

        config.useSingleServer()
                .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
                .setPassword(properties.getPassword())
                .setConnectionPoolSize(properties.getPoolSize())
                .setConnectionMinimumIdleSize(properties.getMinIdleSize())
                .setIdleConnectionTimeout(properties.getIdleTimeout())
                .setConnectTimeout(properties.getConnectTimeout())
                .setRetryAttempts(properties.getRetryAttempts())
                .setRetryInterval(properties.getRetryInterval())
                .setPingConnectionInterval(properties.getPingInterval())
                .setKeepAlive(properties.isKeepAlive())
        ;

        RedissonClient redissonClient = Redisson.create(config);

        logger.info("Elastic Pool registerer(redis) connection initialized. {} {} {}", properties.getHost(), properties.getPoolSize(), !redissonClient.isShutdown());

        return redissonClient;
    }

    @Bean
    public IRegistry redisRegistry(RedissonClient redissonClient) {
        return new RedisRegistry(redissonClient);
    }

    @Bean("elasticPoolService")
    public ElasticPoolService elasticPoolService(ApplicationContext applicationContext, Map<String, ThreadPoolExecutor> threadPoolExecutorMap, RedissonClient redissonClient) {
        String applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");

        if (StringUtils.isBlank(applicationName)) {
            applicationName = "Default";
            logger.warn("ElasticPool Warning: the application name cannot be retrieved, because SpringBoot application has not configured 'spring.application.name'.");
        }

        Set<String> threadPoolKeys = threadPoolExecutorMap.keySet();
        for (String threadPoolKey : threadPoolKeys) {
            ThreadPoolConfigEntity threadPoolConfigEntity = redissonClient.<ThreadPoolConfigEntity>getBucket(RegistryEnumVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey() + "_" + applicationName + "_" + threadPoolKey).get();
            if (null == threadPoolConfigEntity) continue;
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolKey);
            threadPoolExecutor.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
            threadPoolExecutor.setMaximumPoolSize(threadPoolConfigEntity.getMaximumPoolSize());
        }

        return new ElasticPoolService(applicationName, threadPoolExecutorMap);
    }

    @Bean
    public ThreadPoolDataReportJob threadPoolDataReportJob(IElasticPoolService elasticPoolService, IRegistry registry) {
        return new ThreadPoolDataReportJob(elasticPoolService, registry);
    }

    @Bean
    public ThreadPoolConfigAdjustListener threadPoolConfigAdjustListener(IElasticPoolService elasticPoolService, IRegistry registry) {
        return new ThreadPoolConfigAdjustListener(elasticPoolService, registry);
    }

    @Bean(name = "elasticPoolRedisTopic")
    public RTopic threadPoolConfigAdjustListener(RedissonClient redissonClient, ThreadPoolConfigAdjustListener threadPoolConfigAdjustListener) {
        RTopic topic = redissonClient.getTopic(RegistryEnumVO.ELASTIC_POOL_REDIS_TOPIC.getKey() + "_" + applicationName);
        topic.addListener(ThreadPoolConfigEntity.class, threadPoolConfigAdjustListener);
        return topic;
    }
}
