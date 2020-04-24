package com.anymore.printer.elements;

import android.support.annotation.NonNull;

/**
 * 所有可打印的内容元素，如文字，二维码，bitmap等元素
 * T 属于String,bitmap,BarCode,EmptyLines范围内
 * Created by liuyuanmao on 2019/6/21.
 */
public interface Printable<T> {
    @NonNull
    T getContent();
}
