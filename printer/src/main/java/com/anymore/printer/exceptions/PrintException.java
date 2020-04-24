package com.anymore.printer.exceptions;

/**
 * 打印异常类
 * Created by liuyuanmao on 2019/6/22.
 */
public class PrintException extends RuntimeException{

    public PrintException() {
    }

    public PrintException(String message) {
        super(message);
    }

    public PrintException(String message, Throwable cause) {
        super(message, cause);
    }

    public PrintException(Throwable cause) {
        super(cause);
    }

}
