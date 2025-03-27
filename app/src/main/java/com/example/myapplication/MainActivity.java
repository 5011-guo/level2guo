package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText w =findViewById(R.id.weight);
        EditText h =findViewById(R.id.height);
        TextView bmi=findViewById(R.id.bmi);
        TextView suggestionTextview=findViewById(R.id.suggestion);
        Button btn=findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("main", "onClick: ");
                Double weight=Double.parseDouble(w.getText().toString());
                Double height=Double.parseDouble(h.getText().toString());
                Double bmivalue=weight/(height*height);
                String formattedBmi = String.format("%.2f", bmivalue);
                bmi.setText("BMI指数为" + formattedBmi);
                String suggestionText = getsuggestion(bmivalue);
                Log.d("MainActivity", "Suggestion text: " + suggestionText);
                suggestionTextview.setText(suggestionText);
            }
        });
    }
    private String getsuggestion(double bmivalue){
        if(bmivalue <18.5){
            return "健康建议:偏瘦,建议多吃";
        }
        else if(bmivalue>=18.5&&bmivalue<24.9){
            return"健康建议:正常,继续保持";
        }
        else if(bmivalue>=25&&bmivalue<29.9){
            return "健康建议:超重,适当少吃";
        }
        else{
            return "健康建议:1肥胖,多锻炼,少吃";
        }
    }
}