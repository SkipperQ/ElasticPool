package cn.skipperq.elastic.pool.sdk.config;

import cn.skipperq.elastic.pool.sdk.domain.ElasticPoolService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/*
    Dynamic Configuration Entry
 */
@Configuration
public class ElasticPoolAutoConfig {

    private final Logger logger = LoggerFactory.getLogger(ElasticPoolAutoConfig.class);
    @Bean("elasticPoolService")
    public ElasticPoolService elasticPoolService(ApplicationContext applicationContext, Map<String, ThreadPoolExecutor> threadPoolExecutorMap) {
        String applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");

        if (StringUtils.isBlank(applicationName)) {
            applicationName = "Default";
            logger.warn("ElasticPool Warning: the application name cannot be retrieved, because SpringBoot application has not configured 'spring.application.name'.");
        }

        return new ElasticPoolService(applicationName, threadPoolExecutorMap);
    }
}
