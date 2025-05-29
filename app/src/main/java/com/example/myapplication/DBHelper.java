package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
public class DBHelper extends SQLiteOpenHelper {
    private static final int VERSION = 2;
    private static final String DB_NAME = "myrate.db";
    public static final String TB_NAME = "tb_rates";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableSql = "CREATE TABLE " + TB_NAME + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "curname TEXT NOT NULL," +
                "currate REAL NOT NULL," +
                "updatedate TEXT NOT NULL)";
        db.execSQL(createTableSql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
            onCreate(db);
        }
    }
}
