package com.anymore.printer.elements;

import android.graphics.Bitmap;

/**
 * bitmap元素
 * Created by liuyuanmao on 2019/6/22.
 */
public class BitmapElement implements Element<Bitmap> {

    private Bitmap bitmap;
    private Rule rule;

    public BitmapElement(Bitmap bitmap) {
        this(bitmap,Rule.BITMAP_ALIGN_LEFT);
    }

    public BitmapElement(Bitmap bitmap, Rule rule) {
        this.bitmap = bitmap;
        this.rule = rule;
    }

    @Override
    public Rule getRule() {
        return rule;
    }

    @Override
    public Bitmap getContent() {
        return bitmap;
    }

    @Override
    public String toString() {
        return "BitmapElement{" + "bitmap=" + bitmap + ", rule=" + rule + '}';
    }
}
