package org.springcrazy.common.oss;



import lombok.extern.slf4j.Slf4j;
import org.springcrazy.common.cache.CacheNames;
import org.springcrazy.common.tool.CacheUtils;
import org.springcrazy.common.tool.JsonUtils;
import org.springcrazy.common.tool.StringUtils;
import org.springcrazy.core.secure.provider.IClientDetailsService;
import org.springcrazy.core.tool.utils.RedisUtil;
import org.springcrazy.core.tool.utils.SpringUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 文件上传Factory
 *
 * @author Lion Li
 */
@Slf4j
public class OssFactory {

    private static final Map<String, OssClient> CLIENT_CACHE = new ConcurrentHashMap<>();
    private static final ReentrantLock LOCK = new ReentrantLock();

    private static RedisUtil redisUtil;

    static {
        redisUtil = SpringUtil.getBean(RedisUtil.class);
    }
    /**
     * 获取默认实例
     */
    public static OssClient instance() {
        // 获取redis 默认类型
        Object configKey = redisUtil.get(OssConstant.DEFAULT_CONFIG_KEY);
        String key = "";
        if (configKey==null) {
            key = "minio";
        }else{
            key = configKey.toString();
        }
        return instance(key);
    }

    /**
     * 根据类型获取实例
     */
    public static OssClient instance(String configKey) {
        String json = CacheUtils.get(CacheNames.SYS_OSS_CONFIG, configKey);
        if (json == null) {
            json = "{\"createDept\":103,\"createBy\":1,\"createTime\":\"2025-06-04 15:39:38\",\"updateBy\":1,\"updateTime\":\"2025-06-20 00:29:44\",\"ossConfigId\":1,\"configKey\":\"minio\",\"accessKey\":\"2d81hylLifXkQoJY9aBR\",\"secretKey\":\"3liKEYlwG8BGoOt7UuuydouFUW8sfMop7rVf8wUY\",\"bucketName\":\"wlh\",\"prefix\":\"\",\"endpoint\":\"http://116.63.57.4:9090\",\"domain\":\"\",\"isHttps\":\"Y\",\"region\":\"\",\"status\":\"0\",\"ext1\":\"\",\"remark\":\"\",\"accessPolicy\":\"2\"}";

           // throw new OssException("系统异常, '" + configKey + "'配置信息不存在!");
        }
        OssProperties properties = JsonUtils.parseObject(json, OssProperties.class);
        // 使用租户标识避免多个租户相同key实例覆盖
        String key = configKey;
        if (StringUtils.isNotBlank(properties.getTenantId())) {
            key = properties.getTenantId() + ":" + configKey;
        }
        OssClient client = CLIENT_CACHE.get(key);
        // 客户端不存在或配置不相同则重新构建
        if (client == null || !client.checkPropertiesSame(properties)) {
            LOCK.lock();
            try {
                client = CLIENT_CACHE.get(key);
                if (client == null || !client.checkPropertiesSame(properties)) {
                    CLIENT_CACHE.put(key, new OssClient(configKey, properties));
                    log.info("创建OSS实例 key => {}", configKey);
                    return CLIENT_CACHE.get(key);
                }
            } finally {
                LOCK.unlock();
            }
        }
        return client;
    }

}
