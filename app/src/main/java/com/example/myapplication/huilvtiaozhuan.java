package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class huilvtiaozhuan extends AppCompatActivity {

    private  static final String TAG="huilv";
    private EditText inDollar,inEuro,inWon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_huilvtiaozhuan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inDollar=findViewById(R.id.dollar_rate);
        inEuro=findViewById(R.id.euro_rate);
        inWon=findViewById(R.id.won_rate);

        Intent intent=getIntent();
        float dollar=intent.getFloatExtra("key_dollar",0.1f);
        float euro=intent.getFloatExtra("key_euro",0.1f);
        float won=intent.getFloatExtra("key_won",0.1f);

        Log.i(TAG, "onCreate: dollar="+dollar);
        Log.i(TAG, "onCreate: euro="+euro);
        Log.i(TAG, "onCreate: won="+won);

        inDollar.setText(String.valueOf(dollar));
        inEuro.setText(String.valueOf(euro));
        inWon.setText(String.valueOf(won));


    }
    public void  save(View btn){
        String dollarStr=inDollar.getText().toString();
        String euroStr=inEuro.getText().toString();
        String wonStr=inWon.getText().toString();
        Log.i(TAG, "save: dollar="+dollarStr);
        Log.i(TAG, "save: euro="+euroStr);
        Log.i(TAG, "save: won="+wonStr);
        try {
            float dollar =Float.parseFloat(dollarStr);
            float euro=Float.parseFloat(euroStr);
            float won=Float.parseFloat(wonStr);
            Intent retIntent=getIntent();
            Bundle bdl=new Bundle();
            bdl.putFloat("ret_dollar",dollar);
            bdl.putFloat("ret_euro",euro);
            bdl.putFloat("ret_won",won);
            retIntent.putExtras(bdl);
            setResult(3,retIntent);
            finish();
        } catch (NumberFormatException e) {
            Log.e(TAG, "save: 出错",e);
        }

    }
}