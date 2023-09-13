package com.example.chi_tieu_giadinh.taikhoancanhan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.chi_tieu_giadinh.cloudmessing.FcmNotificationsSender;
import com.example.chi_tieu_giadinh.MainActivity;
import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import com.google.firebase.messaging.FirebaseMessaging;

public class CreateNoticeActivity extends AppCompatActivity {

    private Button setNotificationButton;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private ImageView clo;
    private EditText editText;
    private Preferencemanager preferencemanager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notice);
        setNotificationButton = findViewById(R.id.button_tao);
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        editText=findViewById(R.id.edit_nhapnd);
        datePicker = findViewById(R.id.date_picker);
        timePicker = findViewById(R.id.time_picker);
        preferencemanager=new Preferencemanager(getApplicationContext());
        clo=findViewById(R.id.close);
        setListenser();

    }


    private void setListenser(){
       clo.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(getApplicationContext(),TrangCaNhanActivity.class));
           }
       });
        setNotificationButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/all",
                       preferencemanager.getString(Constraints.KEY_NAME)+" "+   timePicker.getHour()+":"+timePicker.getMinute(),
                      editText.getText().toString(), getApplicationContext(), CreateNoticeActivity.this);
               notificationsSender.scheduleNotification(datePicker.getDayOfMonth(), datePicker.getMonth(), datePicker.getYear(),
                       timePicker.getCurrentHour(), timePicker.getCurrentMinute());

               Toast.makeText(CreateNoticeActivity.this, "Sucess", Toast.LENGTH_SHORT).show();
               startActivity(new Intent(getApplicationContext(),MainActivity.class));
           }
       });
    }
}
