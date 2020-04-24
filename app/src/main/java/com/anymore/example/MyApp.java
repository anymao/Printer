package com.anymore.example;

import android.app.Application;
import com.anymore.printer.PrintManager;

/**
 * Created by liuyuanmao on 2019/6/24.
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PrintManager.getInstance().install(this);
    }


    @Override
    public void onTerminate() {
        PrintManager.getInstance().release();
        super.onTerminate();
    }
}
