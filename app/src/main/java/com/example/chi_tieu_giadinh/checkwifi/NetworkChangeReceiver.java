package com.example.chi_tieu_giadinh.checkwifi;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.chi_tieu_giadinh.MainActivity;
import com.example.chi_tieu_giadinh.checkwifi.CheckWifiActivity;

import java.util.List;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private boolean isAlertDialogShowing = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        // Kiểm tra kết nối Internet
        if (isConnectedToInternet(context)) {
            // Nếu có kết nối Internet và MainActivity không đang chạy, khởi động MainActivity
            if (!isMainActivityRunning(context)) {
                startMainActivity(context);
            }
        } else {
            // Nếu AlertDialog chưa được hiển thị, thì hiển thị AlertDialog và đặt isAlertDialogShowing = true
            if (!isAlertDialogShowing) {
                showNoInternetAlertDialog(context);
                isAlertDialogShowing = true;
            }
        }
    }

    // Phương thức kiểm tra kết nối Internet
    private boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            // Lấy thông tin về mạng đang kết nối
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            // Kiểm tra xem có mạng đang kết nối và đang hoạt động (đang kết nối hoặc đang chuẩn bị kết nối) hay không
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
// Nếu không có ConnectivityManager hoặc không có mạng đang kết nối, trả về false
        return false;

    }

    // Phương thức hiển thị AlertDialog từ CheckWifiActivity
    private void showNoInternetAlertDialog(Context context) {
        Intent intent = new Intent(context, CheckWifiActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // Phương thức khởi động MainActivity
    private void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // Phương thức kiểm tra xem MainActivity có đang chạy không
    private boolean isMainActivityRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            // Lấy danh sách các Activity đang chạy trên thiết bị (chỉ lấy 1 Activity đầu tiên)
            List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(1);
            if (!runningTasks.isEmpty()) {
                // Lấy tên của Activity đầu tiên trong danh sách đang chạy
                String topActivity = runningTasks.get(0).topActivity.getClassName();
                // So sánh tên Activity đầu tiên trong danh sách đang chạy với tên của MainActivity
                // Nếu trùng khớp, tức là MainActivity đang chạy, trả về true
                return topActivity.equals(MainActivity.class.getName());
            }
        }
        // Nếu không lấy được danh sách các Activity đang chạy hoặc danh sách rỗng, trả về false
        return false;
    }

    // Reset the isAlertDialogShowing variable to false after showing the AlertDialog
    private void resetAlertDialogStatus() {
        isAlertDialogShowing = false;
    }

}
