package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.HashMap;
import java.util.List;

public class MyAdapter extends ArrayAdapter<HashMap<String, String>> {
    private final int resource;
    private Context context;

    public MyAdapter(@NonNull Context context, int resource, @NonNull List<HashMap<String, String>> objects) {
        super(context, resource, objects);
        this.resource = resource;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        // 视图复用优化（ViewHolder模式）
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(resource, parent, false);

            holder = new ViewHolder();
            holder.titleTextView = convertView.findViewById(R.id.itemTitle);
            holder.detailTextView = convertView.findViewById(R.id.itemDetail);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HashMap<String, String> item = getItem(position);
        if (item != null) {
            // 空值处理（确保不显示null）
            String currency = item.get("currency") != null ? item.get("currency") : "未知币种";
            String rate = item.get("rate") != null ? item.get("rate") : "0.00";

            holder.titleTextView.setText("币种: " + currency);
            holder.detailTextView.setText("汇率: " + rate);

            // 调试日志（确认数据绑定）
            if (position < 5) { // 只打印前5条，避免日志过多
                android.util.Log.d("MyAdapter", "绑定数据 - 位置 " + position + ": " +
                        currency + " - " + rate);
            }
        } else {
            holder.titleTextView.setText("数据异常");
            holder.detailTextView.setText("");
            android.util.Log.e("MyAdapter", "item 为 null，位置: " + position);
        }

        return convertView;
    }

    // ViewHolder模式提高性能
    private static class ViewHolder {
        TextView titleTextView;
        TextView detailTextView;
    }

    // 优化删除方法
    public void removeItem(int position) {
        if (position >= 0 && position < getCount()) {
            HashMap<String, String> item = getItem(position);
            if (item != null) {
                remove(item);
                notifyDataSetChanged();
            }
        }
    }
}