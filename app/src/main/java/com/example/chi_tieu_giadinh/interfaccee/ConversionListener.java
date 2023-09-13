package com.example.chi_tieu_giadinh.interfaccee;

import com.example.chi_tieu_giadinh.model.UserModel;

public interface ConversionListener {
    void onConversionCliked(UserModel user);
    void inittialVideomeeting(UserModel user);
    void inittialAudiomeeting(UserModel user);
}
