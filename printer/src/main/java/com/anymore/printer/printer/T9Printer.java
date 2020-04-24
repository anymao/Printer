package com.anymore.printer.printer;

import android.graphics.Bitmap;
import android.util.Log;
import com.android.print.sdk.Barcode;
import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.anymore.printer.elements.Element;
import com.anymore.printer.elements.EmptyLines;
import com.anymore.printer.elements.Rule;

/**
 * T9打印机实现
 * Created by liuyuanmao on 2019/6/21.
 */
class T9Printer implements IPrinter{

    private static final String TAG = T9Printer.class.getSimpleName();

    final PrinterInstance mPrinter;

    T9Printer(PrinterInstance printer) {
        this.mPrinter = printer;
    }

    @Override
    public void findBlackMark() {
        Log.d(TAG, "findBlackMark()");
        //  走纸到黑标位置
        mPrinter.sendByteData(new byte[]{0x0c});
    }

    @Override
    public void print(Element<?> element) {
        Log.d(TAG, "print: "+element.toString());
        Rule rule = element.getRule();
        Object content = element.getContent();
        print(content,rule);
    }

    private void print(Object content, Rule rule) {
        //打印文本
        if (content instanceof String){
            mPrinter.init();
            mPrinter.setPrinter(PrinterConstants.Command.ALIGN, rule.getPrintAlign());
            mPrinter.setPrinter(PrinterConstants.Command.LINE_HEIGHT, rule.getLineHeight());
            mPrinter.setCharacterMultiple(rule.getWidthScale(), rule.getHeightScale());
            mPrinter.printText((String) content);
            //打印完此段文本后光标定位到新行
            mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_NEWLINE);
        } else if (content instanceof EmptyLines){
            //打印空行
            mPrinter.init();
            mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, ((EmptyLines) content).getLines());
        } else if (content instanceof Barcode){
            //打印条码
            mPrinter.init();
            mPrinter.setPrinter(PrinterConstants.Command.LINE_HEIGHT, rule.getLineHeight());
            mPrinter.setCharacterMultiple(rule.getWidthScale(), rule.getHeightScale());
            mPrinter.setPrinter(PrinterConstants.Command.ALIGN, rule.getPrintAlign());
            mPrinter.printBarCode((Barcode) content);
        } else if (content instanceof Bitmap){
            //打印bitmap
            mPrinter.init();
            mPrinter.setPrinter(PrinterConstants.Command.LINE_HEIGHT, rule.getLineHeight());
            mPrinter.setCharacterMultiple(rule.getWidthScale(), rule.getHeightScale());
            mPrinter.setPrinter(PrinterConstants.Command.ALIGN, rule.getPrintAlign());
            mPrinter.printImage((Bitmap) content,rule.getMarginLeft());
        }
    }

}
