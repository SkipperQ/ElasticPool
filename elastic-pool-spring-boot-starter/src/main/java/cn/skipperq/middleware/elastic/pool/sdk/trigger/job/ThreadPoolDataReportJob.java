package cn.skipperq.middleware.elastic.pool.sdk.trigger.job;


import cn.skipperq.middleware.elastic.pool.sdk.domain.IElasticPoolService;
import cn.skipperq.middleware.elastic.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import cn.skipperq.middleware.elastic.pool.sdk.registry.IRegistry;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * thread pool reporting job
 */
public class ThreadPoolDataReportJob {

    private final Logger logger = LoggerFactory.getLogger(ThreadPoolDataReportJob.class);

    private final IElasticPoolService elasticPoolService;

    private final IRegistry registry;

    public ThreadPoolDataReportJob(IElasticPoolService elasticPoolService, IRegistry registry) {
        this.elasticPoolService = elasticPoolService;
        this.registry = registry;
    }

    @Scheduled(cron = "0/20 * * * * ?")
    public void execReportThreadPoolList() {
        List<ThreadPoolConfigEntity> threadPoolConfigEntities = elasticPoolService.queryThreadPoolList();
        registry.reportThreadPool(threadPoolConfigEntities);
        logger.info("elastic pool report thread pool info: {}", JSON.toJSONString(threadPoolConfigEntities));

        for (ThreadPoolConfigEntity threadPoolConfigEntity : threadPoolConfigEntities) {
            registry.reportThreadPoolConfigParameter(threadPoolConfigEntity);
            logger.info("elastic pool report thread pool config: {}", JSON.toJSONString(threadPoolConfigEntity));
        }

    }

}
