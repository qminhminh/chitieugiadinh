package com.example.chi_tieu_giadinh.model;

public class CommentModel {
    String id,img,name,tx_luan,datetime;

    public CommentModel(String id, String img, String name, String tx_luan, String datetime) {
        this.id = id;
        this.img = img;
        this.name = name;
        this.tx_luan = tx_luan;
        this.datetime = datetime;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTx_luan() {
        return tx_luan;
    }

    public void setTx_luan(String tx_luan) {
        this.tx_luan = tx_luan;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
