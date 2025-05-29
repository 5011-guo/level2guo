package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CalculateActivity extends AppCompatActivity {
    private TextView tvCurrencyName;
    private EditText etAmount;
    private TextView tvResult;
    private Button btnCalculate;
    private double buyRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calculate);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvCurrencyName = findViewById(R.id.tv_currency_name);
        etAmount = findViewById(R.id.et_amount);
        tvResult = findViewById(R.id.tv_result);
        btnCalculate = findViewById(R.id.btn_calculate);
        // 接收参数
        Intent intent = getIntent();
        String currencyName = intent.getStringExtra("CURRENCY_NAME");
        String buyRateStr = intent.getStringExtra("BUY_RATE");
        Log.d(TAG, "接收到的币种: " + currencyName + ", 汇率: " + buyRateStr);
        if (currencyName == null) {
            currencyName = "未知币种";
            Log.w(TAG, "币种名称为null，使用默认值");
        }

        if (buyRateStr == null || buyRateStr.trim().isEmpty()) {
            buyRateStr = "0.0";
            Log.w(TAG, "汇率为null或空，使用默认值0.0");
        }
        tvCurrencyName.setText(currencyName);
        try {
            buyRate = Double.parseDouble(buyRateStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "汇率数据错误", Toast.LENGTH_SHORT).show();
            finish(); // 关闭页面
        }

        // 计算按钮点击事件
        btnCalculate.setOnClickListener(v -> calculate());
    }
    private void calculate() {
        String amountStr = etAmount.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            double amount = Double.parseDouble(amountStr);
            double result = amount * buyRate;
            tvResult.setText(String.format("兑换结果: %.2f", result));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入有效数字", Toast.LENGTH_SHORT).show();
        }

    }
}