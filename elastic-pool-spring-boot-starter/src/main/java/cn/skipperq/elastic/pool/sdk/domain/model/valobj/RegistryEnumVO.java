package cn.skipperq.elastic.pool.sdk.domain.model.valobj;

public enum RegistryEnumVO {

    THREAD_POOL_CONFIG_LIST_KEY("THREAD_POOL_CONFIG_LIST_KEY", "Config List"),
    THREAD_POOL_CONFIG_PARAMETER_LIST_KEY("THREAD_POOL_CONFIG_PARAMETER_LIST_KEY", "Config Parameter List"),
    ELASTIC_POOL_REDIS_TOPIC("ELASTIC_POOL_REDIS_TOPIC", "Listener Topic");

    private final String key;
    private final String desc;

    RegistryEnumVO(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    public String getKey() {
        return key;
    }

    public String getDesc() {
        return desc;
    }


}
