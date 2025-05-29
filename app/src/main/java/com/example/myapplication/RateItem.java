package com.example.myapplication;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RateItem {

    private int id;
    private String curName;
    private float curRate;
    private String updateDate;  // 新增：更新日期字段

    // 无参构造函数
    public RateItem() {
        // 默认构造函数
    }

    // 带参数的构造函数，用于初始化货币名称和汇率
    public RateItem(String curName, float curRate) {
        this.curName = curName;
        this.curRate = curRate;
        this.updateDate = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new Date());
    }

    // 带全部参数的构造函数（可选）
    public RateItem(int id, String curName, float curRate, String updateDate) {
        this.id = id;
        this.curName = curName;
        this.curRate = curRate;
        this.updateDate = updateDate;
    }

    // Getter 和 Setter 方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurName() {
        return curName;
    }

    public void setCurName(String curName) {
        this.curName = curName;
    }

    public float getCurRate() {
        return curRate;
    }

    public void setCurRate(float curRate) {
        this.curRate = curRate;
    }

    // 新增：updateDate 的 Getter 和 Setter 方法
    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    // 可选：重写 toString 方法以便调试
    @Override
    public String toString() {
        return "RateItem{" +
                "id=" + id +
                ", curName='" + curName + '\'' +
                ", curRate=" + curRate +
                ", updateDate='" + updateDate + '\'' +
                '}';
    }
}
