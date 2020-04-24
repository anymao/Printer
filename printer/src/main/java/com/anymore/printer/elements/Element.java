package com.anymore.printer.elements;

/**
 * 打印文档的节点元素
 * Created by liuyuanmao on 2019/6/21.
 */
public interface Element<T> extends Printable<T>{
    /**
     * 打印规则，即对打印内容的位置限定
     */
    Rule getRule();
}
