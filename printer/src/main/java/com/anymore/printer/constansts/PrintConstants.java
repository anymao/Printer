package com.anymore.printer.constansts;

import com.android.print.sdk.PrinterConstants;

/**
 * Created by liuyuanmao on 2019/6/21.
 */
public class PrintConstants {


    /**
     * T9蓝牙打印机名称关键字，用于区分T3、T9打印机
     */
    public static final String T9_PRINTER_PREFIX = "T9";

    //打印机类型
    /**
     * T3打印机
     */
    public static final String PRINT_TYPE_T3 = "T3_PRINT";
    /**
     * T9打印机
     */
    public static final String PRINT_TYPE_T9 = "T9_PRINT";

    //内容排列

    public static final int PRINT_ALIGN_LEFT = PrinterConstants.Command.ALIGN_LEFT;
    public static final int PRINT_ALIGN_CENTER = PrinterConstants.Command.ALIGN_CENTER;
    public static final int PRINT_ALIGN_RIGHT = PrinterConstants.Command.ALIGN_RIGHT;

    /**
     * 蓝牙连接状态描述，对除了{@link PrinterConstants.Connect}之外状态的补充
     * public static class Connect {
     *         public static final int SUCCESS = 101;
     *         public static final int FAILED = 102;
     *         public static final int CLOSED = 103;
     *         public static final int NODEVICE = 104;
     * }
     */
    public static final int BLUETOOTH_CLOSED = 105;//主动关闭本机蓝牙
}
