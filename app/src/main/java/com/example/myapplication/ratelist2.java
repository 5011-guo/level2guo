package com.example.myapplication;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ratelist2 extends ListActivity {

    private static final String TAG = "RateActivity";
    private static final int MSG_UPDATE_LIST = 9;
    private Handler handler;
    private String lastUpdateDate = "";
    private final String DATE_SP_KEY = "lastUpdateDate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.list_content);

        // 初始化Handler
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == MSG_UPDATE_LIST) {
                    List<String> list2 = (List<String>) msg.obj;
                    Log.d(TAG, "更新UI: 列表大小=" + list2.size());
                    ListAdapter adapter2 = new ArrayAdapter<>(
                            ratelist2.this,
                            android.R.layout.simple_list_item_1,
                            list2
                    );
                    setListAdapter(adapter2);
                }
            }
        };

        // 获取当前日期
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());

        // 获取上次更新日期
        SharedPreferences sp = getSharedPreferences("myrate", Context.MODE_PRIVATE);
        lastUpdateDate = sp.getString(DATE_SP_KEY, "");
        Log.i(TAG, "上次更新日期: " + lastUpdateDate);
        Log.i(TAG, "当前日期: " + currentDate);

        // 启动数据获取线程
        new Thread(() -> {
            List<String> dataList = new ArrayList<>();

            try {
                if (currentDate.equals(lastUpdateDate)) {
                    // 日期相同，从数据库获取数据
                    Log.i(TAG, "日期相同，从数据库获取数据");
                    DBManager dbManager = new DBManager(ratelist2.this);
                    List<RateItem> rateItems = dbManager.listAll();

                    if (!rateItems.isEmpty()) {
                        // 将数据库数据添加到列表
                        for (RateItem item : rateItems) {
                            dataList.add(item.getCurName() + " => " + item.getCurRate());
                        }
                        Log.i(TAG, "成功从数据库加载 " + dataList.size() + " 条汇率数据");
                    } else {
                        // 数据库为空，仍需从网络获取
                        Log.w(TAG, "数据库为空，即使日期相同仍需从网络获取数据");
                        fetchDataFromNetwork(dataList, currentDate, sp);
                    }
                } else {
                    // 日期不同，从网络获取数据
                    Log.i(TAG, "日期不同，从网络获取数据");
                    fetchDataFromNetwork(dataList, currentDate, sp);
                }
            } catch (Exception e) {
                Log.e(TAG, "获取数据失败: " + e.getMessage(), e);
                e.printStackTrace();
                dataList.add("获取数据失败: " + e.getMessage());
            }

            Log.d(TAG, "准备更新UI，数据大小: " + dataList.size());
            Message msg = handler.obtainMessage(MSG_UPDATE_LIST, dataList);
            handler.sendMessage(msg);
        }).start();
    }

    private void fetchDataFromNetwork(List<String> dataList, String currentDate, SharedPreferences sp) {
        try {
            // 从网络获取数据
            Document doc = Jsoup.connect("https://www.huilvbiao.com/bank/spdb")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                    .timeout(15000)
                    .get();

            Log.d(TAG, "网页获取成功: " + doc.title());

            Element table = doc.select("table").first();
            if (table != null) {
                Elements rows = table.select("tbody tr");
                List<RateItem> rateList = new ArrayList<>();

                Log.d(TAG, "找到表格，行数: " + rows.size());

                for (Element row : rows) {
                    Element currencyElement = row.selectFirst("th.table-coin span");
                    String currency = (currencyElement != null) ?
                            currencyElement.text().trim() : "未知币种";

                    Elements tds = row.select("td");
                    if (tds.size() >= 1) {
                        String buyRate = tds.get(0).text().trim();

                        if (!buyRate.isEmpty() && buyRate.matches("[0-9.]+")) {
                            dataList.add(currency + " => " + buyRate);
                            rateList.add(new RateItem(currency, Float.parseFloat(buyRate)));
                            Log.d(TAG, "解析成功: " + currency + " => " + buyRate);
                        } else {
                            Log.w(TAG, "无效买入价 - 币种: " + currency + ", 买入价: " + buyRate);
                        }
                    }
                }

                if (!rateList.isEmpty()) {
                    Log.i(TAG, "从网络获取 " + rateList.size() + " 条汇率数据");

                    // 更新数据库
                    DBManager dbManager = new DBManager(ratelist2.this);
                    dbManager.deleteAll();
                    dbManager.addAll(rateList);

                    // 更新日期
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(DATE_SP_KEY, currentDate);
                    editor.apply();

                    Log.i(TAG, "数据库更新成功，日期已更新为: " + currentDate);
                } else {
                    Log.e(TAG, "从网络获取的数据为空");
                }
            } else {
                Log.e(TAG, "未找到汇率表格");
            }
        } catch (Exception e) {
            Log.e(TAG, "网络请求异常: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}