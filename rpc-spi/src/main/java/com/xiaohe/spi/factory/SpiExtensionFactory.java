package com.xiaohe.spi.factory;

import com.xiaohe.spi.annotation.SPI;
import com.xiaohe.spi.annotation.SPIClass;
import com.xiaohe.spi.loader.ExtensionLoader;

import java.util.Optional;

@SPIClass
public class SpiExtensionFactory implements ExtensionFactory {
    @Override
    public <T> T getExtension(final String key, final Class<T> clazz) {
        return Optional.ofNullable(clazz)
                .filter(Class::isInterface)
                .filter(cls -> cls.isAnnotationPresent(SPI.class))
                .map(ExtensionLoader::getExtensionLoader)
                .map(ExtensionLoader::getDefaultSpiClassInstance)
                .orElse(null);
    }
}