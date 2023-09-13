package com.example.chi_tieu_giadinh.interfaccee;

import com.example.chi_tieu_giadinh.model.UserModel;

public interface UserListeners {
    void onUserClickd(UserModel user);
    void inittialVideomeeting(UserModel user);
    void inittialAudiomeeting(UserModel user);
}
