package cn.skipperq.middleware.elastic.pool.sdk.domain;

import cn.skipperq.middleware.elastic.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;

import java.util.List;

public interface IElasticPoolService {
    List<ThreadPoolConfigEntity> queryThreadPoolList();

    ThreadPoolConfigEntity queryThreadPoolConfigByName(String threadPoolName);

    void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity);
}
