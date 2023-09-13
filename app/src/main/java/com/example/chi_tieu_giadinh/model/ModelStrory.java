package com.example.chi_tieu_giadinh.model;

import java.io.Serializable;

public class ModelStrory implements Serializable {
    String name ;
    String video;
    String imgdaidien;
    String text;

    public ModelStrory(String name, String video, String imgdaidien, String text) {
        this.name = name;
        this.video = video;
        this.imgdaidien = imgdaidien;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getImgdaidien() {
        return imgdaidien;
    }

    public void setImgdaidien(String imgdaidien) {
        this.imgdaidien = imgdaidien;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
