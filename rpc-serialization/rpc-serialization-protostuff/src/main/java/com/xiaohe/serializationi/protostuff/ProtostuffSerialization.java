package com.xiaohe.serializationi.protostuff;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.xiaohe.common.exception.SerializerException;
import com.xiaohe.serialization.api.Serialization;
import com.xiaohe.spi.annotation.SPIClass;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dyuproject.protostuff.Schema;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-11 13:05
 */
@SPIClass
public class ProtostuffSerialization implements Serialization {
    private static final Logger logger = LoggerFactory.getLogger(ProtostuffSerialization.class);

    private Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

    private Objenesis objenesis = new ObjenesisStd(true);

    private <T> Schema<T> getSchema(Class<T> cls) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(cls);
            if (schema != null) {
                cachedSchema.put(cls, schema);
            }
        }
        return schema;
    }

    @Override
    public <T> byte[] serialize(T obj) {
        logger.info("execute protostuff serialize");
        if (obj == null) {
            throw new SerializerException("serialize object is null.");
        }
        Class<T> cls = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(cls);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new SerializerException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        logger.info("execute protostuff deserialize.");
        if (data == null) {
            throw new SerializerException("deserialize data is null");
        }
        try {
            T t = objenesis.newInstance(clazz);
            Schema<T> schema = getSchema(clazz);
            ProtostuffIOUtil.mergeFrom(data, t, schema);
            return t;
        } catch (Exception e) {
            throw new SerializerException(e.getMessage(), e);
        }
    }
}
