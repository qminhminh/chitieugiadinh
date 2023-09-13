package com.example.chi_tieu_giadinh.model;

public class FamilyModel {
    String id,img,ten,sinhngay,tuoi,gioitinh,chucvu,noio,email;

    public FamilyModel(String id, String img, String ten, String sinhngay, String tuoi, String gioitinh, String chucvu, String noio, String email) {
        this.id = id;
        this.img = img;
        this.ten = ten;
        this.sinhngay = sinhngay;
        this.tuoi = tuoi;
        this.gioitinh = gioitinh;
        this.chucvu = chucvu;
        this.noio = noio;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getSinhngay() {
        return sinhngay;
    }

    public void setSinhngay(String sinhngay) {
        this.sinhngay = sinhngay;
    }

    public String getTuoi() {
        return tuoi;
    }

    public void setTuoi(String tuoi) {
        this.tuoi = tuoi;
    }

    public String getGioitinh() {
        return gioitinh;
    }

    public void setGioitinh(String gioitinh) {
        this.gioitinh = gioitinh;
    }

    public String getChucvu() {
        return chucvu;
    }

    public void setChucvu(String chucvu) {
        this.chucvu = chucvu;
    }

    public String getNoio() {
        return noio;
    }

    public void setNoio(String noio) {
        this.noio = noio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

