package cn.skipperq.elastic.pool.sdk.trigger.listener;

import cn.skipperq.elastic.pool.sdk.domain.IElasticPoolService;
import cn.skipperq.elastic.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import cn.skipperq.elastic.pool.sdk.registry.IRegistry;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ThreadPoolConfigAdjustListener implements MessageListener<ThreadPoolConfigEntity> {
    private final Logger logger = LoggerFactory.getLogger(ThreadPoolConfigAdjustListener.class);

    private final IElasticPoolService elasticPoolService;

    private final IRegistry registry;

    public ThreadPoolConfigAdjustListener(IElasticPoolService elasticPoolService, IRegistry registry) {
        this.elasticPoolService = elasticPoolService;
        this.registry = registry;
    }

    @Override
    public void onMessage(CharSequence charSequence, ThreadPoolConfigEntity threadPoolConfigEntity) {
        logger.info("elastic-pool adjust thread pool config. thread pool name:{}, core thread num:{}, max thread num:{}", threadPoolConfigEntity.getThreadPoolName(), threadPoolConfigEntity.getPoolSize(), threadPoolConfigEntity.getMaximumPoolSize());
        elasticPoolService.updateThreadPoolConfig(threadPoolConfigEntity);

        List<ThreadPoolConfigEntity> threadPoolConfigEntities = elasticPoolService.queryThreadPoolList();
        registry.reportThreadPool(threadPoolConfigEntities);

        ThreadPoolConfigEntity threadPoolConfigEntityCurrent = elasticPoolService.queryThreadPoolConfigByName(threadPoolConfigEntity.getThreadPoolName());
        registry.reportThreadPoolConfigParameter(threadPoolConfigEntityCurrent);
        logger.info("elastic-pool report thread pool config: {}", threadPoolConfigEntity);

    }
}
