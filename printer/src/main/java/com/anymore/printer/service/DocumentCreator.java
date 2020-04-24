package com.anymore.printer.service;

import com.anymore.printer.elements.Document;

/**
 * Created by liuyuanmao on 2019/6/22.
 */
public interface DocumentCreator {
    /**
     *  如果觉得在外部主线程构建一个文档对象很耗时，可以选择实现此接口，create方法在工作线程调用
     */
    Document create();
}
