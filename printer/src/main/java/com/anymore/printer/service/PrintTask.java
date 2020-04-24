package com.anymore.printer.service;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.print.sdk.PrinterInstance;
import com.anymore.printer.annotations.PrintType;
import com.anymore.printer.elements.Document;
import com.anymore.printer.elements.Element;
import com.anymore.printer.exceptions.PrintException;
import com.anymore.printer.printer.IPrinter;
import com.anymore.printer.printer.IPrinter.Factory;

import java.util.LinkedList;

/**
 * 打印任务,打印任务接收一个打印机实例和一个打印文档实例
 * run()方法中实现了文档打印的逻辑
 * Created by liuyuanmao on 2019/6/21.
 */
class PrintTask implements Runnable {
    private static final String TAG = PrintTask.class.getSimpleName();

    private final @PrintType
    String mPrintType;
    private final Document mDocument;
    private final PrinterInstance mPrinter;

    public PrintTask(@PrintType String printType, @NonNull PrinterInstance printer, @NonNull Document document) {
        mPrintType = printType;
        mPrinter = printer;
        mDocument = document;
    }

    @Override
    public void run() throws PrintException {
        try {
            IPrinter printer = Factory.create(mPrintType, mPrinter);
            LinkedList<Element<?>> list = mDocument.getElements();
            //标准模式下，首先寻黑标位置
            if (mDocument.isStandardMode()) {
                printer.findBlackMark();
            }
            //依次打印文档元素
            for (Element<?> element : list) {
                printer.print(element);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            throw new PrintException("打印失败!", e);
        }
    }

}
