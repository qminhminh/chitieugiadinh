package com.example.chi_tieu_giadinh.model;

public class TongIncomeThangModel {
    String thang;
    int tongThu;
    public TongIncomeThangModel() {
        // Default constructor required for calls to DataSnapshot.getValue(TongIncomeThangModel.class)
    }

    public TongIncomeThangModel(String thang, int tongThu) {
        this.thang = thang;
        this.tongThu = tongThu;
    }

    public String getThang() {
        return thang;
    }

    public void setThang(String thang) {
        this.thang = thang;
    }

    public int getTongThu() {
        return tongThu;
    }

    public void setTongThu(int tongThu) {
        this.tongThu = tongThu;
    }
}
