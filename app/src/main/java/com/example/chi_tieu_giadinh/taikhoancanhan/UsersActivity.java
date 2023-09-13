package com.example.chi_tieu_giadinh.taikhoancanhan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.adapter.UserAdapter;
import com.example.chi_tieu_giadinh.cloudmessing.FcmNotificationsSender;
import com.example.chi_tieu_giadinh.cloudmessing.FirebaseMessagingService;
import com.example.chi_tieu_giadinh.databinding.ActivityUsersBinding;
import com.example.chi_tieu_giadinh.interfaccee.UserListeners;
import com.example.chi_tieu_giadinh.model.UserModel;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity implements UserListeners {
    private ActivityUsersBinding binding;
    private Preferencemanager preferencemanager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferencemanager=new Preferencemanager(getApplicationContext());
        setListenners();
        getUsers();
        getTokenUsers();
    }
    private void setListenners(){
        binding.imageBack.setOnClickListener(v->onBackPressed());
    }
    //Mã này có tác dụng lấy danh sách người dùng từ Firestore và hiển thị chúng trong giao diện người dùng.
    private void getUsers() {

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constraints.KEY_USES_AUTH)
                .get()
                .addOnCompleteListener(task -> {
                    String curentUserId = preferencemanager.getString(Constraints.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        ArrayList<UserModel> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (curentUserId != null && curentUserId.equals(queryDocumentSnapshot.getId())) {
                                continue;
                            }
                            UserModel user = new UserModel();
                            user.name = queryDocumentSnapshot.getString(Constraints.KEY_NAME);
                            user.email = queryDocumentSnapshot.getString(Constraints.KEY_EMAIL);
                            user.image = queryDocumentSnapshot.getString(Constraints.KEY_IMGE);
                            user.token = queryDocumentSnapshot.getString(Constraints.KEY_FCM_TOKEN);
                            user.id = queryDocumentSnapshot.getId();
                            users.add(user);
                        }
                        if (users.size() > 0) {
                           UserAdapter userAdapter = new UserAdapter(users, this);
                            binding.userRecycview.setAdapter(userAdapter);
                            binding.userRecycview.setVisibility(View.VISIBLE);
                        } else {
                            showErrorMessgae();
                        }
                    } else {
                        showErrorMessgae();
                    }
                });
    }

    private void showErrorMessgae(){
        binding.textErrorMessgage.setText(String.format("%s","No user available"));
        binding.textErrorMessgage.setVisibility(View.VISIBLE);
    }
    private void getTokenUsers() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                      //  sendFCMTokenToDatabase(task.getResult());
                    } else {
                        // Handle error while fetching token
                        Toast.makeText(UsersActivity.this, "Failed to get FCM token", Toast.LENGTH_SHORT).show();
                    }
                });
    }



//    private void sendFCMTokenToDatabase(String token){
//        FirebaseFirestore databse=FirebaseFirestore.getInstance();
//        DocumentReference documentReference=databse.collection(Constraints.KEY_USES_AUTH).document(
//                preferencemanager.getString(Constraints.KEY_USER_ID)
//        );
//       preferencemanager.putString(Constraints.KEY_FCM_TOKEN,token);
//        documentReference.update(Constraints.KEY_FCM_TOKEN,token)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Toast.makeText(UsersActivity.this, "token update success", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

    @Override
    public void onUserClickd(UserModel user) {
        Intent intent=new Intent(getApplicationContext(),ChatMessActivity.class);
        intent.putExtra(Constraints.KEY_USER,user);
        startActivity(intent);
        finish();
    }

    @Override
    public void inittialVideomeeting(UserModel user) {
          if(user.token==null||user.token.trim().isEmpty()){
              Toast.makeText(this, "is not avalid for meeting", Toast.LENGTH_SHORT).show();
          }
          else {
           // sendNoticefication();
              Intent intent=new Intent(getApplicationContext(),OutgoingInvitionActivity.class);
              intent.putExtra("user",user);
              intent.putExtra("type","video");
              startActivity(intent);

          }
    }
    private void sendNoticefication() {
       // FirebaseMessagingService.ACTIVITY_SERVICE
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        FcmNotificationsSender notificationsSender=new FcmNotificationsSender("/topics/all",
                preferencemanager.getString(Constraints.KEY_NAME),
                "Đã đang tin mới",getApplicationContext(), UsersActivity.this);
        notificationsSender.SendNotifications();
    }
    @Override
    public void inittialAudiomeeting(UserModel user) {
        if(user.token==null||user.token.trim().isEmpty()){
            Toast.makeText(this, "is not avalid for meeting", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Audio meeting with", Toast.LENGTH_SHORT).show();
        }
    }
}