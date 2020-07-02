package cn.lxt6.service.impl;

import cn.lxt6.config.core.annotation.Auto;
import cn.lxt6.config.core.annotation.bean.Service;
import cn.lxt6.model.util.Par;
import cn.lxt6.service.IDefaultRedisService;
import cn.lxt6.util.StringUtil;
import cn.lxt6.util.json.JsonUtil;
import org.slf4j.Logger;
import redis.clients.jedis.JedisPool;

import static cn.lxt6.service.RedisUtil.getJedis;


/**
 * @author chenzy
 *
 * @since 2020-04-02
 */
@Service
public class DefaultRedisServiceImpl implements IDefaultRedisService {
    @Auto
    private Logger sysErrorLog;

    @Auto
    private JedisPool defaultJedisPool;

    /**
     * 将数据写入内存
     * @param key
     * @param val
     */
    @Override
    public Boolean set(String key, String val) {
//        return setJDKSerialize(key,val,null);
        return setIni(key, val);
    }
    @Override
    public Boolean set(String key, Object val) {
        return set(key, JsonUtil.model2Str(val));
    }
    @Override
    public Boolean set(String key, String val, Integer timeout) {
        return setJDKSerialize(key,val,timeout);
    }
    @Override
    public Boolean set(String key, Object val, Integer timeout) {
        return set(key,JsonUtil.model2Str(val),timeout);
    }
    private Boolean setIni(String key, String val){
        if (StringUtil.isBlankOr(key,val)){
            return false;
        }
        try {
            getJedis(defaultJedisPool,sysErrorLog).ifPresent(jedis -> {
                jedis.set(key,val);
                jedis.close();
            });
            return true;
        }catch (Exception e){
            sysErrorLog.error("redis写入失败。key:{}->value:{}",key,val);
            return false;
        }
    }
    /**
     * 将数据以jdk序列化后写入内存
     * @param key
     * @param val
     */
    private Boolean setJDKSerialize(String key, String val, Integer timeout) {
        byte[] bv = StringUtil.serializeJDK(val);
        Par<Boolean> result = new Par(false);
        getJedis(defaultJedisPool,sysErrorLog).ifPresent(jedis -> {
            jedis.set(key.getBytes(), bv);
            //设置过期时间，单位是秒
            if (timeout!=null){
                jedis.expire(key,timeout);
            }
            result.set(true);
            jedis.close();
        });
        return result.get();
    }


    /**
     * 获取数据
     *
     * @param key
     */
    @Override
    public String get(String key) {
        if (StringUtil.isBlank(key)){
            return null;
        }
        Par<String> result = new Par();
        getJedis(defaultJedisPool,sysErrorLog).ifPresent(jedis -> {
            String string =StringUtil.derializerJDK(jedis.get(key.getBytes()));
            result.set(string);
            jedis.close();
        });
        return result.get();
    }


    /**
     * 获取数据
     *
     * @param key
     */
    @Override
    public <T> T get(String key, Class<T> tClass) {
        String value = get(key);
        return JsonUtil.str2Model(value, tClass).get();
    }

    /**
     * 删除key，删除内存中的数据
     *
     * @param key
     */
    @Override
    public Boolean del(String key) {
        getJedis(defaultJedisPool,sysErrorLog).ifPresent(jedis -> {
            jedis.del(key);
            jedis.close();
        });

        return true;
    }


    /**
     * 将内存数据写入磁盘，redis服务恢复时或者重启自动加载。应用程序暂时没有必要调用
     */
    private void writeToDB() {
       getJedis(defaultJedisPool,sysErrorLog).ifPresent(jedis -> {
           jedis.save();
           jedis.close();
       });
    }
}
