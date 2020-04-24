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
 * T3打印机实现
 * Created by liuyuanmao on 2019/6/21.
 */
class T3Printer implements IPrinter{

    private static final String TAG = "T3Printer";

    final PrinterInstance mPrinter;

    T3Printer(PrinterInstance printer) {
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
        Rule rule = element.getRule();
        Object content = element.getContent();
        print(content,rule);
    }

    private void print(Object content, Rule rule) {
        if (content instanceof String){
            mPrinter.init();
            mPrinter.setPrinter(PrinterConstants.Command.ALIGN, rule.getPrintAlign());
            mPrinter.setPrinter(PrinterConstants.Command.LINE_HEIGHT, rule.getLineHeight());
            mPrinter.printText((String) content);
            mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_NEWLINE);
        } else if (content instanceof EmptyLines){
            mPrinter.init();
            mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, ((EmptyLines) content).getLines());
        }else if (content instanceof Barcode){
            mPrinter.init();
            mPrinter.setPrinter(PrinterConstants.Command.LINE_HEIGHT, rule.getLineHeight());
            mPrinter.setCharacterMultiple(rule.getWidthScale(), rule.getHeightScale());
            mPrinter.setPrinter(PrinterConstants.Command.ALIGN, rule.getPrintAlign());
            mPrinter.printBarCode((Barcode) content);
        } else if (content instanceof Bitmap){
            mPrinter.init();
            mPrinter.setPrinter(PrinterConstants.Command.LINE_HEIGHT, rule.getLineHeight());
            mPrinter.setCharacterMultiple(rule.getWidthScale(), rule.getHeightScale());
            mPrinter.setPrinter(PrinterConstants.Command.ALIGN, rule.getPrintAlign());
            mPrinter.printImage((Bitmap) content,rule.getMarginLeft());
        }
    }


}
