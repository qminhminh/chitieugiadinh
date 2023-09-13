package com.example.chi_tieu_giadinh;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chi_tieu_giadinh.cloudmessing.FcmNotificationsSender;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        EditText title=findViewById(R.id.title);
        EditText messs=findViewById(R.id.message);
        Button button=findViewById(R.id.alldecieced);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!title.getText().toString().isEmpty()&&!messs.getText().toString().isEmpty()){
                     FcmNotificationsSender notificationsSender=new FcmNotificationsSender("/topics/all",
                             title.getText().toString(),
                             messs.getText().toString(),getApplicationContext(),MainActivity2.this);
                     notificationsSender.SendNotifications();

                }else {
                    Toast.makeText(MainActivity2.this, ",b,bvkj", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}