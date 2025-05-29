package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomListActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final String TAG = "CustomListActivity";
    private ListView mylist;
    private ProgressBar progressBar;
    private Handler handler;
    private MyAdapter adapter;
    private TextView emptyView;
    private List<HashMap<String, String>> listItems = new ArrayList<>();
    private DBManager dbManager;
    private SharedPreferences sp;
    private static final String DATE_SP_KEY = "lastRateDate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_list);

        // 初始化控件
        mylist = findViewById(R.id.listView2);
        progressBar = findViewById(R.id.progressBar1);
        emptyView = findViewById(R.id.emptyView);
        mylist.setEmptyView(emptyView);

        // 初始化数据库和SharedPreferences
        dbManager = new DBManager(this);
        sp = getSharedPreferences("myrate", MODE_PRIVATE);

        // 初始化Handler
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                progressBar.setVisibility(View.GONE);
                if (msg.what == 3 && msg.obj != null) {
                    listItems = (List<HashMap<String, String>>) msg.obj;
                    adapter = new MyAdapter(
                            CustomListActivity.this, R.layout.list_item, listItems);
                    mylist.setAdapter(adapter);
                } else {
                    Log.e(TAG, "数据加载失败");
                    emptyView.setText("数据加载失败，请重试");
                }
            }
        };

        // 设置监听器
        mylist.setOnItemClickListener(this);
        mylist.setOnItemLongClickListener(this);

        // 检查并加载数据（优先从数据库，若日期不同则从网络更新）
        checkAndLoadData();
    }

    private void checkAndLoadData() {
        String curDate = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
        String lastDate = sp.getString(DATE_SP_KEY, "");
        Log.i(TAG, "当前日期: " + curDate + ", 上次更新日期: " + lastDate);

        if (curDate.equals(lastDate)) {
            loadDataFromDB();
        } else {
            fetchDataFromWeb(curDate);
        }
    }

    private void loadDataFromDB() {
        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            List<HashMap<String, String>> dataList = new ArrayList<>();
            try {
                // 从数据库获取所有汇率
                List<RateItem> rateItems = dbManager.listAll();
                if (!rateItems.isEmpty()) {
                    for (RateItem item : rateItems) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("currency", item.getCurName());
                        map.put("rate", String.valueOf(item.getCurRate()));
                        dataList.add(map);
                    }
                    Log.i(TAG, "从数据库加载 " + dataList.size() + " 条汇率数据");
                } else {
                    Log.w(TAG, "数据库为空，从网络获取数据");
                    fetchDataFromWeb(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date()));
                    return;
                }
            } catch (Exception e) {
                Log.e(TAG, "数据库加载失败: " + e.getMessage());
                fetchDataFromWeb(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date()));
                return;
            }
            handler.sendMessage(handler.obtainMessage(3, dataList));
        }).start();
    }

    private void fetchDataFromWeb(String curDate) {
        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            List<HashMap<String, String>> dataList = new ArrayList<>();
            try {
                // 网络获取汇率
                Document doc = Jsoup.connect("https://www.huilvbiao.com/bank/spdb")
                        .userAgent("Mozilla/5.0").timeout(15000).get();
                Element table = doc.select("table").first();

                if (table != null) {
                    Log.d(TAG, "找到表格，行数: " + table.select("tr").size());
                    Elements rows = table.select("tbody tr");
                    List<RateItem> rateList = new ArrayList<>();

                    for (int i = 0; i < rows.size(); i++) {
                        Element row = rows.get(i);
                        // 提取币种
                        Element currencyTh = row.selectFirst("th.table-coin span");
                        String currency = currencyTh != null ? currencyTh.text().trim() : "未知币种";
                        // 提取买入价
                        Elements tds = row.select("td");
                        if (tds.size() >= 1) {
                            String buyRate = tds.get(0).text().trim();
                            String processedRate = processRate(buyRate);
                            if (processedRate != null && !processedRate.equals("暂无数据")) {
                                float rate = Float.parseFloat(processedRate);
                                // 添加到数据列表
                                HashMap<String, String> map = new HashMap<>();
                                map.put("currency", currency);
                                map.put("rate", processedRate);
                                dataList.add(map);
                                // 添加到数据库实体类
                                rateList.add(new RateItem(currency, rate));
                            }
                        }
                    }
                    if (!rateList.isEmpty()) {
                        dbManager.deleteAll();
                        dbManager.addAll(rateList);
                        Log.i(TAG, "成功更新数据库，保存 " + rateList.size() + " 条数据");
                    } else {
                        Log.e(TAG, "网络解析无有效数据，不更新数据库");
                    }
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(DATE_SP_KEY, curDate);
                    editor.apply();
                    Log.i(TAG, "已保存更新日期: " + curDate);
                }
            } catch (Exception e) {
                Log.e(TAG, "网络请求失败: " + e.getMessage());
            } finally {
                handler.sendMessage(handler.obtainMessage(3, dataList));
            }
        }).start();
    }

    private String processRate(String rate) {
        if (rate == null || rate.isEmpty()) return "暂无数据";
        String cleanedRate = rate.replaceAll("[^\\d.-]", "");
        if (cleanedRate.equals("0") || cleanedRate.isEmpty()) {
            return "暂无数据";
        }
        return cleanedRate;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, String> item = (HashMap<String, String>) parent.getItemAtPosition(position);
        if (item != null) {
            String currency = item.get("currency");
            String rate = item.get("rate");
            if (currency != null && rate != null) {
                Intent intent = new Intent(this, CalculateActivity.class);
                intent.putExtra("CURRENCY_NAME", currency);
                intent.putExtra("BUY_RATE", rate);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (adapter != null) {
            adapter.removeItem(position);
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放资源
        if (dbManager != null) {
            dbManager = null;
        }
    }
}


