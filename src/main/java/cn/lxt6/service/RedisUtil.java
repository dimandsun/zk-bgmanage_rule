package cn.lxt6.service;

import org.slf4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Optional;

/**
 * @author chenzy
 * @since 2020-06-29
 */
public class RedisUtil {
    private RedisUtil(){}
    /**
     * 获取Jedis，会多次尝试，若获取不到Jedis，则返回null。后续代码会报空指针异常
     * @return
     */
    public static Optional<Jedis> getJedis(JedisPool pool, Logger logger){
        Jedis result = null;
        try {
            result = pool.getResource();
            if (!result.isConnected()){
                result.connect();
            }
        }catch (JedisConnectionException e){
            logger.error("获取redis连接失败，继续获取",e);
            if (result!=null){
                result.close();
            }
            Integer i=1;
            result=getJedis(pool,logger,i);
        }
        return Optional.ofNullable(result);
    }

    /**
     * 递归获取Jedis,5次尝试后不再获取，返回空值
     * @param i
     * @return
     */
    private static Jedis getJedis(JedisPool pool, Logger logger,Integer i){
        if (i>5){
            return null;
        }
        i++;
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            if (!jedis.isConnected()){
                jedis.connect();
            }
            return jedis;
        }catch (JedisConnectionException e){
            logger.error("获取redis连接失败，继续获取,尝试次数："+i,e);
            if (jedis!=null){
                jedis.close();
            }
            return getJedis(pool,logger,i);
        }
    }
}
