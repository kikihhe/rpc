package com.xiaohe.common.exception;

/**
 * @author : 小何
 * @Description : 序列化异常
 * @date : 2023-12-03 16:31
 */
public class SerializerException extends RuntimeException {
    private static final long serialVersionUID = -6783134254669118520L;

    /**
     * Instantiates a new Serializer exception.
     *
     * @param e the e
     */
    public SerializerException(final Throwable e) {
        super(e);
    }

    /**
     * Instantiates a new Serializer exception.
     *
     * @param message the message
     */
    public SerializerException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new Serializer exception.
     *
     * @param message   the message
     * @param throwable the throwable
     */
    public SerializerException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

}
