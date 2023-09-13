package com.example.chi_tieu_giadinh.utiliti;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferencemanager {
    private final SharedPreferences sharedPreferences;
//Khởi tạo một đối tượng Preferencemanager và liên kết nó với tệp SharedPreferences thông qua context được cung cấp.
    public Preferencemanager(Context context){
        sharedPreferences= context.getSharedPreferences(Constraints.KEY_REFERENCE_NAME,Context.MODE_PRIVATE);
    }
    //Lưu giá trị boolean vào SharedPreferences bằng cách sử dụng một khóa duy nhất.
    public void putBoolean(String key,Boolean value){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    public Boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key,false);
    }
   // Lưu giá trị chuỗi vào SharedPreferences bằng cách sử dụng một khóa duy nhất.
    public void putString(String key,String value){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }
    //Trả về giá trị chuỗi từ SharedPreferences dựa trên khóa được cung cấp. Nếu không tìm thấy giá trị, phương thức sẽ trả về null.
    public String getString(String key){
        return sharedPreferences.getString(key,null);
    }
    //Xóa tất cả các giá trị đã lưu trong SharedPreferences.
    public void clear(){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
