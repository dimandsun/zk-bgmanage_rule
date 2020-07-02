package cn.lxt6.service;

import java.util.Map;

/**
 * @author chenzy
 *
 * @since 2020-04-02
 */
public interface IMgRedisService {
    String get(String key);

    Map<String, String> getMap(String key);

    String setMap(String key, Map<String, String> value);

    <T> T get(String key, Class<T> tClass);
}
