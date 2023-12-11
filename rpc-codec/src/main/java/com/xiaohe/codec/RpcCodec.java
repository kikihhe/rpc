package com.xiaohe.codec;

import com.xiaohe.serialization.api.Serialization;
import com.xiaohe.serialization.jdk.JDKSerialization;
import com.xiaohe.spi.loader.ExtensionLoader;

/**
 * @author : 小何
 * @Description : 实现编解码的接口，提供序列化和反序列化的默认方法
 * @date : 2023-12-04 16:00
 */
public interface RpcCodec {
    default Serialization getSerialization(String serializationType) {
        return ExtensionLoader.getExtension(Serialization.class, serializationType);
    }
}
