package cn.lxt6.service;

/**
 * @author chenzy
 *
 * @since 2020-04-02
 */
public interface IDefaultRedisService {
    Boolean set(String key, String val);

    Boolean set(String key, Object val);

    String get(String key);

    <T> T get(String key, Class<T> tClass);

    Boolean del(String key);

    Boolean set(String key, String val, Integer timeout);
    Boolean set(String key, Object val, Integer timeout);


}
