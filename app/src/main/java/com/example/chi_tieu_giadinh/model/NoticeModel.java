package com.example.chi_tieu_giadinh.model;

public class NoticeModel {
    public String id,name,notice,img;

    public NoticeModel(String id, String name, String notice, String img) {
        this.id = id;
        this.name = name;
        this.notice = notice;
        this.img = img;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
