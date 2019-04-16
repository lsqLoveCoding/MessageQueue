import com.lab1.*;
import redis.clients.jedis.Jedis;

import java.io.IOException;


public class TestRedisQueue {
    public static byte[] redisKey = "key".getBytes();
    public static byte[] dstkey = "dstkey".getBytes();

    static {
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void init() throws IOException {
        Jedis jedis = JedisUtil.getJedis();
        for (int i = 1; i <= 10; i++) {
            Message message = new Message(i, "productor产生的第" + i + "个内容");
            try {
                // 存储redis队列，顺序存储
                JedisUtil.lpush(redisKey, ObjectUtil.objectToBytes(message));
                // System.out.println(redisKey.toString());
                // 获取队列数据
                System.out.println(jedis.lrange(redisKey, 0, -1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private static void pop() throws Exception {
        byte[] bytes = JedisUtil.rpop(redisKey);
        Message msg = (Message) ObjectUtil.bytesToObject(bytes);
        if (msg != null) {
            System.out.println("consumer3----message" + msg.getMessageId() + "----" + msg.getMessageContent());
        }
    }

    private static void rpop() throws Exception {
        byte[] bytes = JedisUtil.rpop(redisKey);
        Message msg = (Message) ObjectUtil.bytesToObject(bytes);
        if (msg != null) {
            System.out.println("consumer1----message" + msg.getMessageId() + "----" + msg.getMessageContent());
        }
    }

    private static void brpoplpush() throws Exception {
        Jedis jedis = JedisUtil.getJedis();
        while (true) {
            try {
                // 从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它； 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
                byte[] bytes = JedisUtil.brpoplpush(redisKey, dstkey, 0);
                // 转换为message

                Message msg = (Message) ObjectUtil.bytesToObject(bytes);
                if (msg != null) {
                    System.out.println("consumer2----message" + msg.getMessageId() + "----" + msg.getMessageContent());
                }
                System.out.println(jedis.lrange(redisKey, 0, -1));
                System.out.println(jedis.lrange(dstkey, 0, -1));
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        try {
            //pop();
            //rpop();
            brpoplpush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
