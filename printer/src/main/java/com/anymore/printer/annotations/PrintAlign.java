package com.anymore.printer.annotations;

import android.support.annotation.IntDef;
import com.anymore.printer.constansts.PrintConstants;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 排列方式:居左，居中，居右
 * Created by liuyuanmao on 2019/6/21.
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({PrintConstants.PRINT_ALIGN_LEFT,PrintConstants.PRINT_ALIGN_CENTER,PrintConstants.PRINT_ALIGN_RIGHT})
public @interface PrintAlign {

}
