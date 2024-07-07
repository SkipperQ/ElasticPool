package cn.skipperq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "thread.pool.executor.config", ignoreInvalidFields = true)
public class ThreadPoolConfigProperties {

    /** thread pool parameters */
    private Integer corePoolSize = 20;
    private Integer maxPoolSize = 200;
    private Long keepAliveTime = 10L;
    private Integer blockQueueSize = 5000;
    /*
     * AbortPolicy: Discards the task and throws a RejectedExecutionException.
     * DiscardPolicy: Directly discards the task without throwing an exception.
     * DiscardOldestPolicy: Removes the task that has been in the queue the longest, and then tries to add the new task to the queue.
     * CallerRunsPolicy: If adding the task to the thread pool fails, the main thread executes the task itself.
     *
    */
    private String policy = "AbortPolicy";

}
