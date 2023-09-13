package com.example.chi_tieu_giadinh.model;

public class ThamGiaProfile {
    String img;
    String name;

    public ThamGiaProfile(String img, String name) {
        this.img = img;
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
