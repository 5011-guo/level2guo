package com.example.myapplication;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class huilv extends AppCompatActivity implements Runnable {

    private static final String TAG = "huilv";
    private EditText inputRmb;
    private TextView tvResult;
    float dollarRate = 34.5f;
    float euroRate = 666.6f;
    float wonRate = 345f;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_huilv);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputRmb = findViewById(R.id.input_rmb_edit_text);
        tvResult = findViewById(R.id.result);

        handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                Log.i(TAG, "handleMessage: 接收");
                if (msg.what == 7) {
                    String str = (String) msg.obj;
                    Log.i(TAG, "Handler: str=" + str);
                    tvResult.setText(str);
                }
                super.handleMessage(msg);
            }
        };

        Log.i(TAG, "onCreate: 启动线程");
        Thread t = new Thread(this);
        t.start();
    }

    public void myclick(View btn) {
        Log.i(TAG, "myclick: 22222222");

        String strInput = inputRmb.getText().toString();
        try {
            float inputf = Float.parseFloat(strInput);
            float result = 0;
            if (btn.getId() == R.id.dollar_btn) {
                result = inputf * dollarRate;
            } else if (btn.getId() == R.id.euro_btn) {
                result = inputf * euroRate;
            } else {
                result = inputf * wonRate;
            }

            tvResult.setText(String.valueOf(result));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入正确数据", Toast.LENGTH_SHORT).show();
        }
    }

    public void openconfig(View btn) {
        Intent intent = new Intent(this, huilvtiaozhuan.class);
        intent.putExtra("key_dollar", dollarRate);
        intent.putExtra("key_euro", euroRate);
        intent.putExtra("key_won", wonRate);
        Log.i(TAG, "openConfig:dollarRate " + dollarRate);
        Log.i(TAG, "openConfig:euroRate " + euroRate);
        Log.i(TAG, "openConfig:wonRate " + wonRate);
        startActivityForResult(intent, 6);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 6 && resultCode == 3) {
            if (data != null) {
                Bundle ret = data.getExtras();
                if (ret != null) {
                    dollarRate = ret.getFloat("ret_dollar");
                    euroRate = ret.getFloat("ret_euro");
                    wonRate = ret.getFloat("ret_won");
                    Log.i(TAG, "onActivityResult: dollarRate=" + dollarRate);
                    Log.i(TAG, "onActivityResult: euroRate=" + euroRate);
                    Log.i(TAG, "onActivityResult: wonRate=" + wonRate);
                }
            }
        }
    }
    public void run() {
        Log.i(TAG, "run:running ");
        try {
            Thread.sleep(5000);
            URL url = null;
            try {
                url = new URL("https://www.boc.cn/sourcedb/whpj/");
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                InputStream in = http.getInputStream();

                String html = inputStream2String(in);
                Log.i(TAG, "run: html=" + html);

                Document doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/").get();
                Log.i(TAG, "run: title=" + doc.title());
                Elements tables = doc.getElementsByTag("table");
                Element table = tables.get(1);
                Log.i(TAG, "run: table2=" + table);
                Elements trs = table.getElementsByTag("tr");
                trs.remove(0);
                for(Element tr :trs){
                    Elements tds=tr.children();
                    Element td1=tds.first();
                    Element td2=tds.get(5);
                    String str1=td1.text();
                    String str2=td2.text();
                    Log.i(TAG, "run: "+str1+"==>"+str2);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = handler.obtainMessage(7, "hhhh");
            msg.what = 7;
            msg.obj = "hello from run";
            handler.sendMessage(msg);
            Log.i(TAG, "run: 发送完毕");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private String inputStream2String(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "utf-8");
        while (true) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }
}