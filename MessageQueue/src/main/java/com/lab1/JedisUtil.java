package com.lab1;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.Serializable;
import java.util.List;

/**
 * 利用redis做队列,我们采用的是redis中list的push和pop操作;
 * 结合队列的特点：FIFO:先进先出原则
 * edis中lpush头入(rpop尾出)或rpush尾入(lpop头出)可以满足要求,而Redis中list要push或pop的对象仅需要转换成byte[]即可
 * java采用Jedis进行Redis的存储和Redis的连接池设置
 *
 * @Author HITLSQ
 * @Date 2019/4/15
 */

// 工具类，构建jedis连接池，以及一些队列的相关方法
public class JedisUtil implements Serializable {


    private static final long serialVersionUID = -8845822685363860550L;
    // 定义jedis连接池
    private static JedisPool jedisPool;

    // 静态配置
    static {
        JedisPoolConfig config = new JedisPoolConfig();
        // 在指定时刻通过pool能够获取到的最大的连接的jedis个数
        config.setMaxTotal(5000);
        // 最大能够保持idle（空闲）的数量，控制一个pool最多有多少个状态为idle的jedis实例
        config.setMaxIdle(256);
        // 当连接池内的连接耗尽时，连接会阻塞，超过了阻塞的时间（设定的maxWaitMillis，单位毫秒）时会报错
        config.setMaxWaitMillis(5000L);
        // 在borrow一个jedis实例时，是否提前进行validate操作，这样得到的jedis实例均是可用的
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        // 如果为true，表示有一个idle object evitor线程对idle object进行扫描，如果validate失败，此object会被从pool中drop掉
        config.setTestWhileIdle(true);
        config.setMinEvictableIdleTimeMillis(60000L);
        // idle object evitor两次扫描之间sleep的毫秒数
        config.setTimeBetweenEvictionRunsMillis(3000L);
        config.setNumTestsPerEvictionRun(-1);
        // 配置，主机，端口，超时时间，密码。在安装redis时设置密码为空，所以此处密码为null
        jedisPool = new JedisPool(config, "127.0.0.1", 6379, 60000, null);
    }

    // 获取jedis实例,synchronized防止多个线程同时访问此方法，线程互斥。
    public synchronized static Jedis getJedis() {
        if (jedisPool != null) {
            // 获取jedis实例
            return jedisPool.getResource();
        } else {
            return null;
        }
    }

    // 返还到连接池
    private static void returnJedis(Jedis jedis) {
        try {
            jedisPool.returnResource(jedis);
        } catch (Exception e) {
            if (jedis.isConnected()) {
                jedis.quit();
                jedis.disconnect();
                // jedis.close();
            }
        }
    }

    /**
     * 存储redis队列，顺序存储
     *
     * @param key   redis键名
     * @param value redis键值
     */
    public static void lpush(byte[] key, byte[] value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.lpush(key, value);
        } catch (Exception e) {
            // 释放redis对象
            jedisPool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            // 返还到连接池
            returnJedis(jedis);
        }
    }

    /**
     * 存储redis队列，反序存储
     *
     * @param key   redis键名
     * @param value redis键值
     */
    public static void rpush(byte[] key, byte[] value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.rpush(key, value);
        } catch (Exception e) {
            // 释放redis对象
            jedisPool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            // 返还到连接池
            returnJedis(jedis);
        }
    }

    /**
     * 将列表中的尾元素弹出，并返回给客户端
     *
     * @param key         redis队列
     * @param destination redis队列
     */
    public static void rpoplpush(byte[] key, byte[] destination) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.rpoplpush(key, destination);
        } catch (Exception e) {
            // 释放redis对象
            jedisPool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            // 返还到连接池
            returnJedis(jedis);
        }
    }

    /**
     * 获取队列数据
     *
     * @param key 键名
     * @return
     */
    public static List lpopList(byte[] key) {
        List list = null;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            list = jedis.lrange(key, 0, -1);
        } catch (Exception e) {
            // 释放redis对象
            jedisPool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            // 返还到连接池
            returnJedis(jedis);
        }
        return list;
    }

    /**
     * 获取队列数据
     *
     * @param key 键名
     * @return
     */
    public static byte[] rpop(byte[] key) {

        byte[] bytes = null;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            bytes = jedis.rpop(key);
        } catch (Exception e) {
            // 释放redis对象
            jedisPool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            // 返还到连接池
            returnJedis(jedis);
        }
        return bytes;
    }

    /**
     * 从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它； 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
     *
     * @param srckey
     * @param dstkey
     * @param timout
     * @return
     */
    public static byte[] brpoplpush(byte[] srckey, byte[] dstkey, int timout) {
        byte[] value = null;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            value = jedis.brpoplpush(srckey, dstkey, timout);
        } catch (Exception e) {
            //释放redis对象
            jedisPool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            returnJedis(jedis);
        }
        return value;
    }
}
