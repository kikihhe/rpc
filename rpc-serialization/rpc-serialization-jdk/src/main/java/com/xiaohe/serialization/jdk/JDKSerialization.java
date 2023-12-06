package com.xiaohe.serialization.jdk;

import com.xiaohe.common.exception.SerializerException;
import com.xiaohe.serialization.api.Serialization;

import java.io.*;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-03 22:16
 */
public class JDKSerialization implements Serialization {

    /**
     * JDK序列化
     * @param obj
     * @return
     * @param <T>
     */
    @Override
    public <T> byte[] serialize(T obj) {
        if (obj == null) {
            throw new SerializerException("serialize object is null");
        }
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new SerializerException(e.getMessage(), e);
        }
    }

    /**
     * JDK反序列化
     * @param data
     * @param clazz
     * @return
     * @param <T>
     */
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        if (data == null) {
            throw new SerializerException("deserialize data is null");
        }
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (T) ois.readObject();
        } catch (Exception e) {
            throw new SerializerException(e.getMessage(), e);
        }
    }
}
