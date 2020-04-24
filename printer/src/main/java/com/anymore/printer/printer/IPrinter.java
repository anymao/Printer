package com.anymore.printer.printer;

import com.android.print.sdk.PrinterInstance;
import com.anymore.printer.annotations.PrintType;
import com.anymore.printer.elements.Element;
import com.anymore.printer.constansts.PrintConstants;

/**
 * 打印机打印接口
 * Created by liuyuanmao on 2019/6/21.
 */
public interface IPrinter {

    /**
     * 寻找黑标，这是打印的前提
     */
    void findBlackMark();

    /**
     * 打印文档节点元素
     * @param element 节点元素
     */
    void print(Element<?> element);

    class Factory {

        /**
         * T9打印机实例
         */
        private static T9Printer T9PrinterInstance;
        /**
         * T3打印机实例
         */
        private static T3Printer T3PrinterInstance;


        /**
         * 第一次打印，会创建IPrinter的实现并且静态变量中保存，如果这时候断开连接，PrinterManager中的
         * PrinterInstance会被置空，然后再连接，生成的这个PrinterInstance应该与之前是不同的对象，
         * 创建IPrinter实例的时候应该采用新的PrinterInstance实例
         * @param printType 打印类型
         * @param instance 当前正在连接的打印机
         * @return
         */
        public static IPrinter create(@PrintType String printType, PrinterInstance instance) {
            switch (printType) {
                case PrintConstants.PRINT_TYPE_T9:
                    return getT9PrinterInstance(instance);
                case PrintConstants.PRINT_TYPE_T3:
                    return getT3PrinterInstance(instance);
                default:
                    throw new RuntimeException("No such type printer");
            }
        }

        private static T9Printer getT9PrinterInstance(PrinterInstance instance) {
            if (T9PrinterInstance == null || T9PrinterInstance.mPrinter != instance) {
                T9PrinterInstance = new T9Printer(instance);
            }
            return T9PrinterInstance;
        }

        private static T3Printer getT3PrinterInstance(PrinterInstance instance) {
            if (T3PrinterInstance == null || T3PrinterInstance.mPrinter != instance) {
                T3PrinterInstance = new T3Printer(instance);
            }
            return T3PrinterInstance;
        }

        /**
         * 断开连接的时候，将打印机实例置空
         */
        public static void clear(){
            T3PrinterInstance = null;
            T9PrinterInstance = null;
        }
    }
}
