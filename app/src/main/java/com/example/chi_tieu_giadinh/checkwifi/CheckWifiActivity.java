package com.example.chi_tieu_giadinh.checkwifi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.chi_tieu_giadinh.MainActivity;
import com.example.chi_tieu_giadinh.R;

public class CheckWifiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_wifi);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start MainActivity after a delay of 2 seconds
                // startMainActivity();
            }
        }, 2000);
    }
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Finish this activity so that the user can't navigate back to it
    }
}