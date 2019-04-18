package com.lab1;

import java.io.*;

/**
 * @Author HITLSQ
 * @Date 2019/4/15
 */

// 支持Java序列化的工具类，主要功能是将对象转化为byte数组，以及将byte数组反序列化为Java对象
// 序列化的作用：保存在内存中的各种对象的状态（也就是实例变量，不是方法），并且可以把保存的对象状态再读出来
// 每一个需要序列化的对象都需要实现Serializable接口
public class ObjectUtil {

    /**
     * 将Java对象转换为byte数组
     *
     * @param obj
     * @return bytes
     * @throws IOException
     */
    public static byte[] objectToBytes(Object obj) throws IOException {
        //字节数组输出流在内存中创建一个字节数组缓冲区，所有发送到输出流的数据保存在该字节数组缓冲区中
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // ObjectOutputStream可以将一个对象转换成二进制流，写入到指定的OutputStream；然后可以通过ObjectInputStream将二进制流还原成对象
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        objectOutputStream.close();
        // 返回byte数组
        return bytes;
    }

    /**
     * 将byte数组转换为Java对象
     *
     * @param bytes
     * @return obj
     * @throws IOException,ClassNotFoundException
     */
    public static Object bytesToObject(byte[] bytes) throws IOException, ClassNotFoundException {
        // 包含一个内部缓冲区，其中包含可以从流中读取的字节
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        // 对象输入流
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        // 读取对象
        return objectInputStream.readObject();
    }
}
