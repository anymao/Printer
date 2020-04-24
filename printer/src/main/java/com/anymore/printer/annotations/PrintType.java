package com.anymore.printer.annotations;

import android.support.annotation.StringDef;
import com.anymore.printer.constansts.PrintConstants;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 打印机类型：T3 or T9
 * Created by liuyuanmao on 2019/6/21.
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef({PrintConstants.PRINT_TYPE_T3,PrintConstants.PRINT_TYPE_T9})
public @interface PrintType {

}
