package com.anymore.printer.elements;

import com.android.print.sdk.Barcode;

/**
 * 条码元素
 * Created by liuyuanmao on 2019/6/22.
 */
public class BarcodeElement implements Element<Barcode> {

    private Barcode barcode;
    private Rule rule;

    public BarcodeElement(Barcode barcode) {
        this(barcode,Rule.BARCODE_ALIGN_CENTER);
    }

    public BarcodeElement(Barcode barcode, Rule rule) {
        this.barcode = barcode;
        this.rule = rule;
    }

    @Override
    public Rule getRule() {
        return rule;
    }

    @Override
    public Barcode getContent() {
        return barcode;
    }

    @Override
    public String toString() {
        return "BarcodeElement{" + "barcode=" + barcode + ", rule=" + rule + '}';
    }
}
