package com.anymore.example;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by liuyuanmao on 2019/6/24.
 */
public class TestActivity extends AppCompatActivity {

    private Button btnStart;
    private TextView text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        btnStart = findViewById(R.id.btn_start);
        text = findViewById(R.id.text);
        ExecutorService service = Executors.newSingleThreadExecutor();
        btnStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Future<String> future = service.submit(new TextTask());
                try {

                    text.setText(future.get());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.toast).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TestActivity.this,"hhhh",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private static class TextTask implements Callable<String>{
        private static int count = 0;
        @Override
        public String call() throws Exception {

            Log.i("TAG", "call: "+ Thread.currentThread().getName());

            Thread.sleep(15000);
            return "this is result<"+(++count)+">!!!";
    }
    }
}
