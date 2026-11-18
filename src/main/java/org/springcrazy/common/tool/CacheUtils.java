package org.springcrazy.common.tool;

import org.springcrazy.core.tool.utils.Func;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheUtils {
    private static CacheManager manager;

    @Autowired
    public void setManager(CacheManager manager) {
        CacheUtils.manager = manager;
    }

    public static Object get(Object key) {
        Cache.ValueWrapper cache = manager.getCache("cache").get(key);
        if(Func.isNotEmpty(cache)){
            return cache.get();
        }
        return null;
    }

    /**
     * 获取缓存值
     *
     * @param cacheNames 缓存组名称
     * @param key        缓存key
     */
    public static <T> T get(String cacheNames, Object key) {
        Cache.ValueWrapper wrapper = manager.getCache(cacheNames).get(key);
        return wrapper != null ? (T) wrapper.get() : null;
    }

    public static void put( Object key, Object value) {
        manager.getCache("cache").put(key,value);
    }

    public static boolean remove(Object key) {
        return manager.getCache("cache").evictIfPresent(key);
    }

    public static void removeAll(String cacheName) {
        manager.getCache("cache").clear();
    }


}
