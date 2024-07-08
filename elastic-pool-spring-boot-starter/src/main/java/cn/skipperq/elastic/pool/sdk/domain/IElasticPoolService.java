package cn.skipperq.elastic.pool.sdk.domain;

import cn.skipperq.elastic.pool.sdk.domain.model.ThreadPoolConfigEntity;

import java.util.List;

public interface IElasticPoolService {
    List<ThreadPoolConfigEntity> queryThreadPoolList();

    ThreadPoolConfigEntity queryThreadPoolConfigByName(String threadPoolName);

    void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity);
}
