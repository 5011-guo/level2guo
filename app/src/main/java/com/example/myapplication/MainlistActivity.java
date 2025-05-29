package com.example.myapplication;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainlistActivity extends AppCompatActivity {

    private GridView gridView;
    private TextView noDataView;
    private MyAdapter adapter;
    private List<HashMap<String, String>> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mainlist);

        // 设置窗口边距适配
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化控件
        gridView = findViewById(R.id.gridView);
        noDataView = findViewById(R.id.noData);
        gridView.setEmptyView(noDataView);

        // 设置点击事件（示例）
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            HashMap<String, String> item = dataList.get(position);
            Toast.makeText(this, "点击了: " + item.get("currency"), Toast.LENGTH_SHORT).show();
        });

        // 模拟数据（正式环境可替换为网络请求）
        loadSampleData();
    }

    private void loadSampleData() {
        // 清空旧数据
        dataList.clear();

        // 添加模拟数据
        for (int i = 1; i <= 20; i++) {
            HashMap<String, String> item = new HashMap<>();
            item.put("currency", "币种 " + i);
            item.put("rate", "汇率 " + (i * 0.1));
            dataList.add(item);
        }

        // 创建并设置适配器
        adapter = new MyAdapter(this, R.layout.list_item, dataList);
        gridView.setAdapter(adapter);
    }
}