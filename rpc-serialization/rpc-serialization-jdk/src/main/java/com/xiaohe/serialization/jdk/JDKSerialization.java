package com.xiaohe.serialization.jdk;

import com.xiaohe.common.exception.SerializerException;
import com.xiaohe.serialization.api.Serialization;
import com.xiaohe.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-03 22:16
 */
@SPIClass
public class JDKSerialization implements Serialization {
    private static Logger logger = LoggerFactory.getLogger(JDKSerialization.class);
    /**
     * JDK序列化
     * @param obj
     * @return
     * @param <T>
     */
    @Override
    public <T> byte[] serialize(T obj) {
        logger.info("execute jdk serialize");
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
        logger.info("execute jdk deserialize");
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
