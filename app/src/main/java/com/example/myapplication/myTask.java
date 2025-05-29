package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class myTask extends AppCompatActivity {
    private static final String TAG = "Rate";
    private Handler handler;
    private Bundle retbundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainlist); // 假设布局文件名为 activity_my_task.xml

        // 初始化 Handler
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Bundle data = msg.getData();
                String ratesData = data.getString("data");
                // 处理接收到的汇率数据
                Log.d(TAG, "Received rates data: " + ratesData);
            }
        };

        // 启动子线程进行网络请求
        new Thread(this::fetchCurrentRates).start();
    }

    public void fetchCurrentRates() {
        Log.i(TAG, "fetchCurrentRates: 子线程开始执行");

        ArrayList<String> list = new ArrayList<>();
        try {
            Document doc = Jsoup.connect("https://www.huilvbiao.com/bank/spdb")
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();
            Element tables = doc.select("table").first();
            if (tables != null) {
                Elements trs = tables.getElementsByTag("tr");
                StringBuilder dataBuilder = new StringBuilder();
                for (Element tr : trs) {
                    Elements tds = tr.children();
                    if (tds.size() >= 5) {
                        String currencyname = tds.get(0).text().trim();
                        String buyRateStr = tds.get(1).text().trim();
                        String sellRateStr = tds.get(2).text().trim();

                        if (!buyRateStr.isEmpty() && buyRateStr.matches("[0-9.]+")
                                && !sellRateStr.isEmpty() && sellRateStr.matches("[0-9.]+")) {
                            Log.i("currencydata", currencyname + "==>" + buyRateStr);
                            dataBuilder.append(currencyname).append("==>").append(buyRateStr).append("\n");
                        } else {
                            Log.w("currencydata", "Invalid data for " + currencyname);
                        }
                    }
                }
                retbundle.putString("data", dataBuilder.toString());
                Message msg = new Message();
                msg.setData(retbundle);
                handler.sendMessage(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("currencydata", "抓取失败" + e.getMessage());
            runOnUiThread(() ->
                    Toast.makeText(this, "抓取失败:" + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
        }
    }
}
