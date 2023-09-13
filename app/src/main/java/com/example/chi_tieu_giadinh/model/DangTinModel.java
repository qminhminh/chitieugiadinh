package com.example.chi_tieu_giadinh.model;

public class DangTinModel {
    String id,imgDaidien,imgDangTin,video,name,edit,time;
    long count;

    public DangTinModel(String id, String imgDaidien, String imgDangTin, String video, String name, String edit, String time, long count) {
        this.id = id;
        this.imgDaidien = imgDaidien;
        this.imgDangTin = imgDangTin;
        this.video = video;
        this.name = name;
        this.edit = edit;
        this.time = time;
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgDaidien() {
        return imgDaidien;
    }

    public void setImgDaidien(String imgDaidien) {
        this.imgDaidien = imgDaidien;
    }

    public String getImgDangTin() {
        return imgDangTin;
    }

    public void setImgDangTin(String imgDangTin) {
        this.imgDangTin = imgDangTin;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEdit() {
        return edit;
    }

    public void setEdit(String edit) {
        this.edit = edit;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
