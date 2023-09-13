//package com.example.chi_tieu_giadinh.network;
//
//import android.content.Intent;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//
//import com.example.chi_tieu_giadinh.cloudmessing.FirebaseMessagingService;
//import com.example.chi_tieu_giadinh.taikhoancanhan.IncomeingInvitationActivity;
//import com.example.chi_tieu_giadinh.utiliti.Constraints;
//import com.google.firebase.messaging.RemoteMessage;
//
//public class FcmCall extends FirebaseMessagingService {
//
//
//    @Override
//    public void onNewToken(@NonNull String token) {
//        super.onNewToken(token);
//    }
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//        String type=remoteMessage.getData().get(Constraints.REMOTE_MSG_TYPE);
//
//
//
//        if(type.equals(Constraints.REMOTE_MSG_INVITATION)){
//            Intent intent=new Intent(getApplicationContext(), IncomeingInvitationActivity.class);
//            intent.putExtra(Constraints.REMOTE_MSG_MEETING_TYPE,remoteMessage.getData().get(Constraints.REMOTE_MSG_MEETING_TYPE));
//            intent.putExtra(Constraints.KEY_NAME,remoteMessage.getData().get(Constraints.KEY_NAME));
//            intent.putExtra(Constraints.KEY_EMAIL,remoteMessage.getData().get(Constraints.KEY_EMAIL));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        }
//
//        else {
//            Toast.makeText(this, "false ==============================", Toast.LENGTH_SHORT).show();
//        }
//    }
//}
