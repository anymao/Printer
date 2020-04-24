package com.anymore.printer.executors;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 打印机线程池管理
 * Created by liuyuanmao on 2019/6/11.
 */
@RestrictTo(Scope.LIBRARY)
public class PrintExecutors {

    public final ExecutorService printWorkExecutor;

    public final ExecutorService workExecutor;

    public final Executor uiExecutor;

    public PrintExecutors() {
       //默认打印任务是顺序执行的，所以打印线程池是一个单线程池
        this(Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r,"print-executor-thread");
            }
        }),Executors.newCachedThreadPool(),new MainExecutor());
    }

    public PrintExecutors(ExecutorService printWorkExecutor,ExecutorService workExecutor, Executor uiExecutor) {
        this.printWorkExecutor = printWorkExecutor;
        this.workExecutor = workExecutor;
        this.uiExecutor = uiExecutor;
    }

    public static PrintExecutors getInstance(){
        return Holder.INSTANCE;
    }

    private static class Holder{
        private static final PrintExecutors INSTANCE = new PrintExecutors();
    }

    private static final class MainExecutor implements Executor{
        private Handler uiHandler = new Handler(Looper.getMainLooper());
        @Override
        public void execute(Runnable command) {
            uiHandler.post(command);
        }
    }
}
