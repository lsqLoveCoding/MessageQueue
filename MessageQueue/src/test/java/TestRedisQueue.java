import com.lab1.*;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import static com.oracle.jrockit.jfr.ContentType.Bytes;


public class TestRedisQueue implements Serializable {


    private static final long serialVersionUID = 5160330772233672531L;
    public static byte[] redisKey = "key".getBytes();
    public static byte[] dstkey = "dstkey".getBytes();

    static {
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] listTobyte(List<Byte> list) {
        if (list == null || list.size() < 0)
            return null;
        byte[] bytes = new byte[list.size()];
        int i = 0;
        Iterator<Byte> iterator = list.iterator();
        while (iterator.hasNext()) {
            bytes[i] = iterator.next();
            i++;
        }
        return bytes;
    }

    private static void init() throws IOException {
        Jedis jedis = JedisUtil.getJedis();
        for (int i = 1; i <= 10; i++) {
            Message message = new Message(i, "productor message" + i);
            try {
                // 存储redis队列，顺序存储
                JedisUtil.lpush(redisKey, ObjectUtil.objectToBytes(message));
                // System.out.println(redisKey.toString());
                // 获取队列数据
                String key = new String(redisKey);
                System.out.println(key);
                System.out.println(jedis.lrange(redisKey, 0, -1).hashCode());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /*private static void rpoplpush() throws Exception {
        byte[] bytes = JedisUtil.rpoplpush(redisKey, dstkey);
        Message msg = (Message) ObjectUtil.bytesToObject(bytes);
        if (msg != null) {
            System.out.println("consumer3----message" + msg.getMessageId() + "----" + msg.getMessageContent());
        }
    }*/

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
                // System.out.println(jedis.lrange(redisKey, 0, -1));
                // System.out.println(jedis.lrange(dstkey, 0, -1));
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        try {
            //pop();
            rpop();
            brpoplpush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
