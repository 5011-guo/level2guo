package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
public class zuoyejifen extends AppCompatActivity {
    private static final String TAG = "zuoyejifen";
    private int scoreA;
    private int scoreB;
    private TextView showscoreA;
    private TextView showscoreB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_zuoyejifen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        showscoreA = findViewById(R.id.Ascore);
        showscoreB = findViewById(R.id.Bscore);
        Button addthreeA = findViewById(R.id.threeA);
        addthreeA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "点击了队伍 A 的 3 分按钮");
                addPointsToTeamA(3);
            }
        });
        Button addtwoA = findViewById(R.id.twoA);
        addtwoA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "点击了队伍 A 的 2 分按钮");
                addPointsToTeamA(2);
            }
        });
        Button addoneA = findViewById(R.id.freeA);
        addoneA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "点击了队伍 A 的 1 分按钮");
                addPointsToTeamA(1);
            }
        });
        Button addthreeB = findViewById(R.id.threeB);
        addthreeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "点击了队伍 B 的 3 分按钮");
                addPointsToTeamB(3);
            }
        });
        Button addtwoB = findViewById(R.id.twoB);
        addtwoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "点击了队伍 B 的 2 分按钮");
                addPointsToTeamB(2);
            }
        });
        Button addoneB = findViewById(R.id.freeB);
        addoneB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "点击了队伍 B 的 1 分按钮");
                addPointsToTeamB(1);
            }
        });
        Button resetButton = findViewById(R.id.reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "点击了重置按钮");
                resetScores();
            }
        });
    }
    private void addPointsToTeamA(int points) {
        scoreA += points;
        showscoreA.setText(String.valueOf(scoreA));
        Log.i(TAG, "队伍 A 增加了 " + points + " 分，当前得分: " + scoreA);
    }
    private void addPointsToTeamB(int points) {
        scoreB += points;
        showscoreB.setText(String.valueOf(scoreB));
        Log.i(TAG, "队伍 B 增加了 " + points + " 分，当前得分: " + scoreB);
    }
    private void resetScores() {
        scoreA = 0;
        scoreB = 0;
        showscoreA.setText(String.valueOf(scoreA));
        showscoreB.setText(String.valueOf(scoreB));
        Log.i(TAG, "分数已重置，队伍 A 得分: " + scoreA + "，队伍 B 得分: " + scoreB);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("key1",scoreA);
        outState.putInt("key2",scoreB);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        scoreA=savedInstanceState.getInt("key1");
        scoreB=savedInstanceState.getInt("key2");
        showscoreA.setText(String.valueOf(scoreA));
        showscoreB.setText(String.valueOf(scoreB));
    }
}
