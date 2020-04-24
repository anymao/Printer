package com.anymore.printer;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.GuardedBy;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.android.print.sdk.PrinterConstants.Connect;
import com.android.print.sdk.PrinterInstance;
import com.android.print.sdk.bluetooth.BluetoothPort;
import com.android.print.sdk.util.Utils;
import com.anymore.printer.annotations.PrintType;
import com.anymore.printer.constansts.PrintConstants;
import com.anymore.printer.executors.PrintExecutors;
import com.anymore.printer.service.PrintService;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Created by liuyuanmao on 2019/6/24.
 */
public final class PrintManager {

    private static final String TAG = "PrintManager";

    private Context mApplicationContext;
    private BluetoothAdapter mBluetoothAdapter;
    private PrinterInstance mPrinterInstance;
    private PrintService mPrintService;
    private String mPrintType;

    private boolean isConnected;
    @GuardedBy("mListenersLock")
    private List<ConnectListener> mConnectListeners;
    private final Object mListenersLock = new Object();
    private BluetoothStatusReceiver mStatusReceiver;

    private PrintManager() {
        mConnectListeners = new ArrayList<>();
    }

    private static class Holder {

        @SuppressLint("StaticFieldLeak")
        final static PrintManager INSTANCE = new PrintManager();
    }

    public static PrintManager getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 初始化
     */
    public void install(Context context) {
        mApplicationContext = context.getApplicationContext();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            throw new RuntimeException(
                    "This device don`t have bluetooth or Bluetooth is not supported on this hardware platform");
        }

        //注册广播监听
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        mStatusReceiver = new BluetoothStatusReceiver();
        mApplicationContext.registerReceiver(mStatusReceiver,filter);
    }


    public void register(ConnectListener listener){
        mConnectListeners.add(listener);
    }

    public void unregister(ConnectListener listener){
        mConnectListeners.remove(listener);
    }

    /**
     * 开启蓝牙
     */
    public void turnOnBluetooth() {
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
    }

    /**
     * 获取已配对的蓝牙打印机设备
     */
    public List<BluetoothDevice> getPairedBluetoothPrinter() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        List<BluetoothDevice> pairedPrinter = new ArrayList<>();
        for (BluetoothDevice device : pairedDevices) {
            if (isPrinter(device)) {
                pairedPrinter.add(device);
            }
        }
        return pairedPrinter;
    }

    /**
     * 判断蓝牙设备是不是打印机
     * @param device 蓝牙设备
     */
    private boolean isPrinter(BluetoothDevice device) {
        return device.getBluetoothClass().getMajorDeviceClass()
                == BluetoothClass.Device.Major.IMAGING;
    }


    /**
     * 自动连接打印机，连接的是上一次连接的打印机设备(如果存在)
     */
    public void autoConnectWithPrinter(){
        Properties macProperties = Utils.getBtConnInfo(mApplicationContext);
        if (macProperties != null){
            String mac = macProperties.getProperty("mac");
            if (!TextUtils.isEmpty(mac)){
                connectWithPrinter(mBluetoothAdapter.getRemoteDevice(mac));
            }
        }
    }

    /**
     * 蓝牙打印机连接
     */
    public void connectWithPrinter(@NonNull final BluetoothDevice device) {
        if (!isConnected){
            PrintExecutors.getInstance().workExecutor
                    .submit(() -> {
                        if (device.getName().contains(PrintConstants.T9_PRINTER_PREFIX)){
                            mPrintType = PrintConstants.PRINT_TYPE_T9;
                        }else {
                            mPrintType = PrintConstants.PRINT_TYPE_T3;
                        }
                        Utils.saveBtConnInfo(mApplicationContext,device.getAddress());
                        mPrinterInstance =  new BluetoothPort().btConnnect(mApplicationContext,device.getAddress(),mBluetoothAdapter,mPrinterConnectHandler);
                    });
        }
    }

    /**
     * 蓝牙打印机是否已经连接
     * @return 是否连接
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * 连接蓝牙打印机成功后会构造一个PrintService对象，此对象有自己的打印机类型{@link PrintConstants#PRINT_TYPE_T3}
     * 或者{@link PrintConstants#PRINT_TYPE_T9}
     * 蓝牙打印机连接未完成或者失败时候，返回null
     * @return
     */
    @Nullable
    public PrintService getPrintService(){
        return mPrintService;
    }

    /**
     * @return 当前的打印类型，如果未连接，打印类型为空
     */
    @PrintType
    @Nullable
    public String currentPrintType(){
        return mPrintType;
    }


    /**
     * 导航至蓝牙配对界面
     */
    public void navigateToBluetoothSetting(){
        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mApplicationContext.startActivity(intent);
    }

    /**
     * 断开打印机连接
     */
    public void close(){
        if (mPrinterInstance != null && mPrinterInstance.isConnected()){
            mPrinterInstance.closeConnection();
            mPrinterInstance = null;
            mPrintService = null;
        }
    }

    public void release(){
        mApplicationContext.unregisterReceiver(mStatusReceiver);
    }


    private Handler mPrinterConnectHandler = new Handler(Looper.getMainLooper(), msg -> {
        switch (msg.what) {
            case Connect.SUCCESS://打印机连接成功
                if (mPrinterInstance != null) {
                    mPrinterInstance.init();
                    mPrintService = new PrintService(mPrinterInstance,mPrintType);
                    isConnected = true;
                    notifyConnectSuccess();
                }
                Toast.makeText(mApplicationContext, "连接成功", Toast.LENGTH_LONG).show();
                break;
            case Connect.FAILED:
                isConnected = false;
                mPrintType = null;
                notifyDisconnect("打印机连接失败");
                Toast.makeText(mApplicationContext, "连接失败", Toast.LENGTH_LONG).show();
                break;
            case Connect.CLOSED://打印机关闭
                isConnected = false;
                mPrinterInstance = null;
                mPrintService = null;
                mPrintType = null;
                notifyDisconnect("打印机关闭");
                Toast.makeText(mApplicationContext, "打印机关闭", Toast.LENGTH_LONG).show();
                break;
            case PrintConstants.BLUETOOTH_CLOSED://关闭本机蓝牙
                isConnected = false;
                mPrinterInstance = null;
                mPrintService = null;
                mPrintType = null;
                notifyDisconnect("蓝牙关闭");
                Toast.makeText(mApplicationContext, "蓝牙关闭", Toast.LENGTH_LONG).show();
                break;
            case Connect.NODEVICE:
                isConnected = false;
                notifyDisconnect("没有打印机 ");
                Toast.makeText(mApplicationContext, "没有打印机设备", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
        return true;
    });


    /************蓝牙连接监听广播****************/
    private class BluetoothStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        Log.d(TAG, "正在配对...");
                        break;
                    case BluetoothDevice.BOND_BONDED: //蓝牙配对成功
                        Log.d(TAG, "配对完成");
                        if (!isConnected && isPrinter(device)) {//如果当前未连接，且当前配对完成的设备是打印机设备，进行打印机连接
                            connectWithPrinter(device);
                        }
                        break;
                    case BluetoothDevice.BOND_NONE: //蓝牙配对关闭
                        Log.d(TAG, "没有配对");
                        break;
                    default:
                        break;
                }
            } else if (TextUtils.equals(action, BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "state on 手机蓝牙开启");
                        break;
                    case BluetoothAdapter.STATE_OFF://主动关闭本机蓝牙，通知打印机连接状态为连接断开
                        Log.d(TAG, "state off 手机蓝牙关闭");
                        mPrinterConnectHandler.obtainMessage(PrintConstants.BLUETOOTH_CLOSED).sendToTarget();
                        break;
                    default:
                        break;
                }
            } else if (TextUtils.equals(action, BluetoothDevice.ACTION_ACL_DISCONNECTED)) {//打印机关闭
                Log.d(TAG, "acl disconnected 蓝牙打印机失去连接");
                mPrinterConnectHandler.obtainMessage(Connect.CLOSED).sendToTarget();
            }
        }
    }

    /**
     * 通知所有监听器，打印机连接成功
     */
    private void notifyConnectSuccess(){
        synchronized (mListenersLock){
            for (ConnectListener listener : mConnectListeners) {
                listener.onConnectSuccess();
            }
        }
    }

    /**
     * 通知所有监听器，连接断开或者失败
     * @param message
     */
    private void notifyDisconnect(String message){
        synchronized (mListenersLock){
            for (ConnectListener listener : mConnectListeners) {
                listener.onDisconnect(message);
            }
        }
    }

    public interface ConnectListener{
        void onConnectSuccess();

        /**
         * 连接断开
         * @param message 断开或者连接未成功的原因描述
         */
        void onDisconnect(String message);
    }
}
