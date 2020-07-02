package cn.lxt6.service.impl;

import cn.lxt6.config.core.annotation.Auto;
import cn.lxt6.config.core.annotation.bean.Service;
import cn.lxt6.model.util.Par;
import cn.lxt6.service.IMgRedisService;
import cn.lxt6.util.StringUtil;
import cn.lxt6.util.json.JsonUtil;
import org.slf4j.Logger;
import redis.clients.jedis.JedisPool;

import java.util.Map;

import static cn.lxt6.service.RedisUtil.getJedis;


/**
 * @author chenzy
 * @since 2020-04-02
 *
 */
@Service
public class MgRedisServiceImpl implements IMgRedisService {
    @Auto
    private Logger sysErrorLog;
    @Auto
    private JedisPool mgJedisPool;
    @Override
    public String get(String key) {
        Par<String> result = new Par();
        getJedis(mgJedisPool,sysErrorLog).ifPresent(jedis -> {
            result.set(jedis.get(key));
            jedis.close();
        });
        return StringUtil.str2JsonStr(result.get());
    }
    /**
     * 获取数据
     *
     * @param key
     */
    @Override
    public <T> T get(String key, Class<T> tClass) {
        Par<String> result = new Par();
        getJedis(mgJedisPool,sysErrorLog).ifPresent(jedis -> {
            result.set(jedis.get(key));
            jedis.close();
        });
        return JsonUtil.str2Model(result.get(), tClass).get();
    }
    @Override
    public Map<String, String> getMap(String key){
        Par<Map<String, String>> result = new Par();
        getJedis(mgJedisPool,sysErrorLog).ifPresent(jedis -> {
            result.set(jedis.hgetAll(key));
            jedis.close();
        });
        return result.get();
    }
    @Override
    public String setMap(String key,Map<String, String> value){
        Par<String> result = new Par();
        getJedis(mgJedisPool,sysErrorLog).ifPresent(jedis -> {
            result.set(jedis.hmset(key, value));
            jedis.close();
        });
        return result.get();
    }



    /**
     * 将内存数据写入磁盘，redis服务恢复时或者重启自动加载。应用程序暂时没有必要调用
     */
    private void writeToDB() {
        getJedis(mgJedisPool,sysErrorLog).ifPresent(jedis -> {
            jedis.save();
            jedis.close();
        });
    }


}
