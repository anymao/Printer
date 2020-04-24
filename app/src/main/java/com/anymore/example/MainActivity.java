package com.anymore.example;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.print.sdk.Barcode;
import com.android.print.sdk.PrinterConstants;
import com.anymore.printer.PrintManager;
import com.anymore.printer.PrintManager.ConnectListener;
import com.anymore.printer.elements.Document;
import com.anymore.printer.constansts.PrintConstants;
import com.anymore.printer.service.DocumentCreator;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ConnectListener {

    private static final String TAG = "MainActivity";
    private PrintManager manager;
    private AlertDialog loadingDialog;
    private TextView tvBluetoothStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        startActivity(new Intent(this, TestActivity.class));
        initDialog();
        manager = PrintManager.getInstance();
        tvBluetoothStatus = findViewById(R.id.tv_bluetooth_status);
        tvBluetoothStatus.setText(manager.isConnected()?"打印机已经连接":"打印机未连接");
        manager.register(this);
    }

    @Override
    protected void onDestroy() {
        manager.unregister(this);
        super.onDestroy();
    }

    private void initDialog() {
        loadingDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("请稍后")
                .setMessage("正在打印...").create();
    }


    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_open_bluetooth:
                manager.turnOnBluetooth();
                break;
            case R.id.btn_connect:
                connectWithFirstDevice();
//                manager.autoConnectWithPrinter();
                break;
            case R.id.btn_print:
                startActivity(new Intent(this,Print2Activity.class));
                break;
//                Document document = createDocument();
//                PrintService service = manager.getPrintService();
//                if (service != null) {
////                    service.print(document);
////                    loadingDialog.show();
////                    service.print(getDocumentCreator(), new PrintListener() {
////                        @Override
////                        public void onPrintSuccess() {
////                            loadingDialog.dismiss();
////                            Toast.makeText(MainActivity.this,"打印成功!",Toast.LENGTH_SHORT).show();
////                        }
////
////                        @Override
////                        public void onPrintFailed(String message, Throwable throwable) {
////                            loadingDialog.dismiss();
////                            Toast.makeText(MainActivity.this,"打印失败!",Toast.LENGTH_SHORT).show();
////                        }
////                    });
//                    service.print(getDocumentCreator()).doOnSubscribe(
//                            disposable -> loadingDialog.show()).observeOn(AndroidSchedulers.mainThread()).subscribe(
//                            () -> {
//                                loadingDialog.dismiss();
//                                Toast.makeText(MainActivity.this, "打印成功!", Toast.LENGTH_SHORT).show();
//                            }, throwable -> {
//                                loadingDialog.dismiss();
//                                Toast.makeText(MainActivity.this, "打印失败!", Toast.LENGTH_SHORT).show();
//                            });
//
//                } else {
//                    Toast.makeText(this, "请稍后蓝牙打印机初始化...", Toast.LENGTH_LONG).show();
//                }
//                break;
        }
    }

    private void connectWithFirstDevice() {
        List<BluetoothDevice> devices = manager.getPairedBluetoothPrinter();
        if (!devices.isEmpty()) {
            manager.connectWithPrinter(devices.get(0));
        }else {
            manager.navigateToBluetoothSetting();
        }

    }

    private Document createDocument() {
        return new Document.Builder()
                .addEmptyLines(3)
                .addText("编号:4101031591583021", PrintConstants.PRINT_ALIGN_CENTER)
                .addBarcode(new Barcode(PrinterConstants.BarcodeType.CODE128, 2, 60, 0, "4101031591583021"))
                .addText("准驾车型:小型汽车(打印机测试)")
                .addEmptyLines(4)
                .addText("驾驶证号码:411524199705036019")
                .addEmptyLines(3)
                .addText("当事人于2019年6月22日在杨金路智慧公园北门30米处，违法停放机动车。根据《中华人民共和国道路交通安全法》第八十七条第二款之规定，决定对当事人给予口头警告后放行。")
                .build();
    }

    private Bitmap converGrayBitmap(Bitmap input) {
        int threshold = 100;
        int width = input.getWidth(); // 获取位图的宽
        int height = input.getHeight(); // 获取位图的高
        int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组
        // 设定二值化的域值，默认值为100
        //tmp = 180;
        input.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];
                // 分离三原色
                alpha = ((grey & 0xFF000000) >> 24);
                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);
                if (red < threshold) {
                    red = 0;
                } else {
                    red = 255;
                }
                if (blue < threshold) {
                    blue = 0;
                } else {
                    blue = 255;
                }
                if (green < threshold) {
                    green = 0;
                } else {
                    green = 255;
                }
                pixels[width * i + j] = alpha << 24 | red << 16 | green << 8 | blue;
                if (pixels[width * i + j] == -1) {
                    pixels[width * i + j] = -1;
                } else {
                    pixels[width * i + j] = -16777216;
                }
            }
        }
        // 新建图片
        Bitmap newBmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        // 设置图片数据
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
        return newBmp;
    }

    private DocumentCreator getDocumentCreator() {
        return () -> {
            Bitmap policeSignBitmap = null;
            try {
                Bitmap signBitmap = BitmapFactory.decodeStream(getAssets().open("sign.png"));
                if (signBitmap != null) {
                    Bitmap graySign = converGrayBitmap(signBitmap);
                    if (graySign != null) {
                        policeSignBitmap = createBitmap("交通警察：", graySign, "警号: 111111");
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            return new Document.Builder()
                    .addEmptyLines(4)
                    .addText("郑州市公安局交通警察支队第三大队", PrintConstants.PRINT_ALIGN_CENTER)
                    .addEmptyLines(1)
                    .addText("公安交通管理简易程序处罚决定书", PrintConstants.PRINT_ALIGN_CENTER, 0, 1)
                    .addEmptyLines(1)
                    .addText("编号： 4101031591583021", PrintConstants.PRINT_ALIGN_CENTER)
                    .addBarcode(new Barcode(PrinterConstants.BarcodeType.CODE128, 2, 60, 0, "4101031591583021"))
                    .addEmptyLines(1)
                    .addText("被处罚人:孔令杰")
                    .addText("机动车驾驶证档案编号:410331288493")
                    .addText("机动车驾驶证/居民身份证编号:410306196412030512")
                    .addText("准驾车型：C1        联系方式：")
                    .addText("车辆牌号：豫A50957  车辆类型：小型汽车")
                    .addText("发证机关：河南省洛阳市公安局交通警察支队车辆管理所")
                    .addEmptyLines(2)
                    .addText("  被处罚人于2018年09月11日09时26分，在河南省郑州市京广南路京广路，此次省略很多字，在河南省郑州市京广南路京广路，此次省略很多字，在河南省郑州市京广南路京广路，此次省略很多字，在河南省郑州市京广南路京广路，此次省略很多字，在河南省郑州市京广南路京广路，此次省略很多字，在河南省郑州市京广南路京广路，此次省略很多字，在河南省郑州市京广南路京广路，此次省略很多字。")
                    .addEmptyLines(2)
                    .addText("  持本决定书在15日内到郑州邮政储蓄银行，持本决定书在15日内到郑州邮政储蓄银行持本决定书在15日内到郑州邮政储蓄银行持本决定书在15日内到郑州邮政储蓄银行持本决定书在15日内到郑州邮政储蓄银行。")
                    .addEmptyLines(2)
                    .addBitmap(policeSignBitmap)
                    .addText("2019年06月22日", PrintConstants.PRINT_ALIGN_RIGHT)
                    .addText("被处罚人签名:__________________")
                    .addEmptyLines(2)
                    .addText("备注:__________________________")
                    .addEmptyLines(3)
                    .build();
        };
    }

    public static Bitmap createBitmap(String prefix, Bitmap bitmap, String postfix) {
        if (bitmap == null) {
            return null;
        }
        bitmap = zoomImage(bitmap, 110, 50);
        int width, height;
        height = bitmap.getHeight();
        width = bitmap.getWidth() + 580;
        int fontSize = 23;
        Bitmap retBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(retBitmap);
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(fontSize);
        canvas.drawText(prefix, 0, (fontSize + height) / 2, paint);
        Rect rect = new Rect();
        paint.getTextBounds(prefix, 0, prefix.length(), rect);
        int twidth = rect.width();
        canvas.drawBitmap(bitmap, twidth, 0, paint);
        canvas.drawText(postfix, 120 + twidth, (fontSize + height) / 2, paint);
        canvas.save();
        return retBitmap;
    }

    /**
     * 根据制定的宽高压缩图片
     */
    public static Bitmap zoomImage(Bitmap bitmap, float newWidth, float newHeight) {
        Matrix matrix = new Matrix();
        //获取图片的宽度和高度
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        //计算缩放率
        float scalWidth = newWidth / width;
        float scalHeight = newHeight / height;
        matrix.postScale(scalWidth, scalHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    @Override
    public void onConnectSuccess() {
        tvBluetoothStatus.setText("打印机已连接hhhhh");
    }

    @Override
    public void onDisconnect(String message) {
        tvBluetoothStatus.setText(message);
    }
}
