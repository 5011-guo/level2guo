package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private DBHelper dbHelper;
    private String tableName;

    public DBManager(Context context) {
        dbHelper = new DBHelper(context);
        tableName = DBHelper.TB_NAME;
    }


    public void add(RateItem item) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("curname", item.getCurName());
            values.put("currate", item.getCurRate());
            values.put("updatedate", item.getUpdateDate());
            db.insert(tableName, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }


    public void addAll(List<RateItem> list) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction(); // 开启事务
            for (RateItem item : list) {
                ContentValues values = new ContentValues();
                values.put("curname", item.getCurName());
                values.put("currate", item.getCurRate());
                values.put("updatedate", item.getUpdateDate());
                db.insert(tableName, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    public void deleteAll() {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.delete(tableName, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }


    public void delete(int id) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.delete(tableName, "id=?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }


    public void update(RateItem item) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("curname", item.getCurName());
            values.put("currate", item.getCurRate());
            values.put("updatedate", item.getUpdateDate());
            db.update(tableName, values, "id=?", new String[]{String.valueOf(item.getId())});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }


    public List<RateItem> listAll() {
        List<RateItem> rateList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(tableName, null, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    RateItem item = new RateItem();
                    item.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    item.setCurName(cursor.getString(cursor.getColumnIndex("curname")));
                    item.setCurRate(cursor.getFloat(cursor.getColumnIndex("currate")));
                    item.setUpdateDate(cursor.getString(cursor.getColumnIndex("updatedate")));
                    rateList.add(item);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return rateList;
    }

    public RateItem findById(int id) {
        RateItem rateItem = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(tableName, null, "id=?",
                    new String[]{String.valueOf(id)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                rateItem = new RateItem();
                rateItem.setId(cursor.getInt(cursor.getColumnIndex("id")));
                rateItem.setCurName(cursor.getString(cursor.getColumnIndex("curname")));
                rateItem.setCurRate(cursor.getFloat(cursor.getColumnIndex("currate")));
                rateItem.setUpdateDate(cursor.getString(cursor.getColumnIndex("updatedate")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return rateItem;
    }

    public String getLastUpdateDate() {
        String lastDate = "";
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(tableName,
                    new String[]{"updatedate"},
                    null,
                    null,
                    null,
                    null,
                    "updatedate DESC",
                    "1");
            if (cursor != null && cursor.moveToFirst()) {
                lastDate = cursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return lastDate;
    }
}
