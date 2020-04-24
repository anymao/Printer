package com.anymore.printer.service;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.print.sdk.PrinterInstance;
import com.anymore.printer.annotations.PrintType;
import com.anymore.printer.elements.Document;
import com.anymore.printer.executors.PrintExecutors;

import java.util.List;
import java.util.concurrent.ExecutorService;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liuyuanmao on 2019/6/22.
 */
public final class PrintService {

    private static final String TAG = "PrintService";

    private final @PrintType
    String mPrintType;

    /**
     * 打印线程，默认采用单线程打印
     */
    private final ExecutorService mPrintWorkExecutor;
    /**
     * sdk打印机实例
     */
    private final PrinterInstance mPrinterInstance;

    public PrintService(PrinterInstance instance, @PrintType String printType) {
        this(instance, PrintExecutors.getInstance().printWorkExecutor, printType);
    }

    public PrintService(PrinterInstance instance, ExecutorService printWorkExecutor,
                        @PrintType String printType) {
        this.mPrinterInstance = instance;
        this.mPrintWorkExecutor = printWorkExecutor;
        this.mPrintType = printType;
    }

    /**
     * @return 打印机类型
     */
    public String getPrintType() {
        return mPrintType;
    }

//    public void print(@NonNull Document document){
//        print(document,null);
//    }

    /**
     * 打印文档，通过{@link PrintListener} 监听打印过程
     */
    public void print(@NonNull Document document, @Nullable PrintListener listener) {
        try {
            mPrintWorkExecutor.submit(() -> {
                new PrintTask(mPrintType, mPrinterInstance, document).run();
                if (listener != null) {
                    PrintExecutors.getInstance().uiExecutor.execute(listener::onPrintSuccess);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            if (listener != null) {
                listener.onPrintFailed("打印失败!", e);
            }
        }
    }

    public Completable print(@NonNull Document document) {
        return Completable.fromRunnable(new PrintTask(mPrintType, mPrinterInstance, document))
                .subscribeOn(Schedulers.from(mPrintWorkExecutor));
    }

//    public void print(@NonNull DocumentCreator creator) throws PrintException{
//        print(creator,null);
//    }

    public void print(@NonNull DocumentCreator creator, @Nullable PrintListener listener) {
        try {
            mPrintWorkExecutor.submit(() -> {
                new PrintTask(mPrintType, mPrinterInstance, creator.create()).run();
                if (listener != null) {
                    PrintExecutors.getInstance().uiExecutor.execute(listener::onPrintSuccess);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            if (listener != null) {
                listener.onPrintFailed("打印失败!", e);
            }
        }
    }

    public Completable print(@NonNull DocumentCreator creator) {
        return Completable.fromRunnable(
                () -> new PrintTask(mPrintType, mPrinterInstance, creator.create()).run())
                .subscribeOn(Schedulers.from(mPrintWorkExecutor));
    }

    public void print(@NonNull List<Document> documents, @Nullable PrintListener listener) {
        try {
            mPrintWorkExecutor.submit(() -> {
                for (Document document : documents) {
                    new PrintTask(mPrintType, mPrinterInstance, document).run();
                }
                if (listener != null) {
                    PrintExecutors.getInstance().uiExecutor.execute(listener::onPrintSuccess);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            if (listener != null) {
                listener.onPrintFailed("打印失败!", e);
            }
        }
    }

    public Completable print(@NonNull List<Document> documents) {
        return Completable.fromRunnable(() -> {
            for (Document document : documents) {
                new PrintTask(mPrintType, mPrinterInstance, document).run();
            }
        }).subscribeOn(Schedulers.from(mPrintWorkExecutor));
    }


    /**
     * 打印回调
     */
    public interface PrintListener {

//        @MainThread
//        void onPrintStart();

        @MainThread
        void onPrintSuccess();

        @MainThread
        void onPrintFailed(String message, Throwable throwable);
    }
}
