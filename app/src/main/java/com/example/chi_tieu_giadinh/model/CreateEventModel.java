package com.example.chi_tieu_giadinh.model;

import java.io.Serializable;

public class CreateEventModel  {
     String img;
     String name;
     String date;
     String time;
     String noidung;
     String ghichu;

     public CreateEventModel(String img, String name, String date, String time, String noidung, String ghichu) {
          this.img = img;
          this.name = name;
          this.date = date;
          this.time = time;
          this.noidung = noidung;
          this.ghichu = ghichu;
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

     public String getDate() {
          return date;
     }

     public void setDate(String date) {
          this.date = date;
     }

     public String getTime() {
          return time;
     }

     public void setTime(String time) {
          this.time = time;
     }

     public String getNoidung() {
          return noidung;
     }

     public void setNoidung(String noidung) {
          this.noidung = noidung;
     }

     public String getGhichu() {
          return ghichu;
     }

     public void setGhichu(String ghichu) {
          this.ghichu = ghichu;
     }
}
