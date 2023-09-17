package com.example.chi_tieu_giadinh.taikhoancanhan;

import androidx.appcompat.app.AppCompatActivity;
import com.example.chi_tieu_giadinh.MainActivity;
import com.example.chi_tieu_giadinh.cloudmessing.FcmNotificationsSender;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.chi_tieu_giadinh.databinding.ActivityCreteEventBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.HashMap;

public class CreteEventActivity extends AppCompatActivity {

    ActivityCreteEventBinding binding;
    Preferencemanager preferencemanager;

    FirebaseFirestore firestore=FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCreteEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
         preferencemanager=new Preferencemanager(getApplicationContext());
        setListener();
    }
    public  void setListener(){
        binding.buttonTao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFirebase();
                sendNoticefication();
            }
        });

    }
    public void addFirebase(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        preferencemanager.putString(Constraints.KEY_EVENT_NOIDUNG,binding.editNhapnd.getText().toString());
        HashMap<Object,String> add=new HashMap<>();
        add.put(Constraints.KEY_NAME,preferencemanager.getString(Constraints.KEY_NAME));
       add.put(Constraints.KEY_EVENT_NGAY, String.valueOf(binding.datePicker.getDayOfMonth()+"/"+binding.datePicker.getYear()));
       add.put(Constraints.KEY_EVENT_TIME, String.valueOf(binding.timePicker.getHour()+":"+binding.timePicker.getMinute()));
       add.put(Constraints.KEY_EVENT_NOIDUNG,binding.editNhapnd.getText().toString());
       add.put(Constraints.KEY_EVENT_GHICHU,binding.editGhichu.getText().toString());
       add.put(Constraints.KEY_IMGE,preferencemanager.getString(Constraints.KEY_IMGE));
       firestore.collection(Constraints.KEY_COLLECTIONS_EVENT).document(uid)
               .set(add).addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void unused) {
                       startActivity(new Intent(CreteEventActivity.this, MainActivity.class));
                       finish();
                   }
               });
    }
    private void sendNoticefication() {
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        FcmNotificationsSender notificationsSender=new FcmNotificationsSender("/topics/all",
                preferencemanager.getString(Constraints.KEY_NAME),
                preferencemanager.getString(Constraints.KEY_EVENT_NOIDUNG),getApplicationContext(), CreteEventActivity.this);
        notificationsSender.SendNotifications();
    }



}