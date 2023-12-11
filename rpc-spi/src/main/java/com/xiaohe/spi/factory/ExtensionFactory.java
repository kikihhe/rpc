package com.xiaohe.spi.factory;

import com.xiaohe.spi.annotation.SPI;

@SPI("spi")
public interface ExtensionFactory {

    /**
     * 获取扩展类对象
     * @param <T>  泛型类型
     * @param key  传入的key值
     * @param clazz Class类型对象
     * @return 扩展类对象
     */
    <T> T getExtension(String key, Class<T> clazz);
}