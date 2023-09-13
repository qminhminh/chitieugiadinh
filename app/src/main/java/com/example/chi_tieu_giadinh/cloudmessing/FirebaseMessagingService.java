package com.example.chi_tieu_giadinh.cloudmessing;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.example.chi_tieu_giadinh.MainActivity;
import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.taikhoancanhan.IncomeingInvitationActivity;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
     NotificationManager mNotificationManager;
    private Preferencemanager preferencemanager;

    @Override
    public void onNewToken(@NonNull String token
    ) {
        super.onNewToken(token);
        Log.d("FCM_TOKEN","token:"+token);
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        preferencemanager=new Preferencemanager(getApplicationContext());
        //String type=remoteMessage.getData().get(Constraints.REMOTE_MSG_TYPE);
         String type=preferencemanager.getString(Constraints.REMOTE_MSG_TYPE);
//       if (type==Constraints.REMOTE_MSG_INVITATION) {
//        String token=preferencemanager.getString(Constraints.KEY_FCM_TOKEN);
//
//            Intent intent = new Intent(getApplicationContext(), IncomeingInvitationActivity.class);
//            intent.putExtra(Constraints.REMOTE_MSG_MEETING_TYPE, preferencemanager.getString(Constraints.REMOTE_MSG_MEETING_TYPE));
//            intent.putExtra(Constraints.KEY_NAME, preferencemanager.getString(Constraints.KEY_NAME));
//            intent.putExtra(Constraints.KEY_EMAIL, preferencemanager.getString(Constraints.KEY_EMAIL));
//            intent.putExtra(Constraints.REMOTE_MSG_INVITOR_TOKEN,remoteMessage.getData().get(Constraints.REMOTE_MSG_INVITOR_TOKEN));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add thisline to set the flag
//            getApplicationContext().startActivity(intent);
//
//
//          }else if (type==Constraints.REMOTE_MSG_INVITATION_REPONSE){
//            Intent intentt=new Intent(Constraints.REMOTE_MSG_INVITATION_REPONSE);
//            intentt.putExtra(Constraints.REMOTE_MSG_INVITATION_REPONSE,preferencemanager.getString(Constraints.REMOTE_MSG_INVITATION_REPONSE));
//            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentt);
//       }




// Phát âm thanh và rung:
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();// âm thanh được phát.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            r.setLooping(false);
        }

        // vibration:Đối tượng Vibrator được khởi tạo từ SystemService để tạo rung.
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 300, 300, 300};
        v.vibrate(pattern, -1);
//Xây dựng thông báo:
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID");


      //Kiểm tra phiên bản Android đang chạy để xác định xem có hỗ trợ các tùy chọn biểu tượng nhỏ theo kiểu Material Design (từ Android Lollipop trở lên) hay không.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.baseline_notifications_none_24);
        } else {
            builder.setSmallIcon(R.drawable.baseline_notifications_none_24);
        }

        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE);

        builder.setContentTitle(remoteMessage.getNotification().getTitle());
        builder.setContentText(remoteMessage.getNotification().getBody());
        builder.setContentIntent(pendingIntent);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()));
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_MAX);

        mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

// notificationId is a unique int for each notification that you must define
        mNotificationManager.notify(100, builder.build());


        //notice call

    }
    private Bitmap getUserImage(String encodedImage){
        byte[] bytes= Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

}


