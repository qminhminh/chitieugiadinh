package com.example.chi_tieu_giadinh.taikhoancanhan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.databinding.ActivityOutgoingInvitionBinding;
import com.example.chi_tieu_giadinh.model.UserModel;
import com.example.chi_tieu_giadinh.network.ApiCilent;
import com.example.chi_tieu_giadinh.network.ApiService;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import com.google.firebase.messaging.FirebaseMessaging;
import org.json.JSONArray;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutgoingInvitionActivity extends AppCompatActivity {
  private ActivityOutgoingInvitionBinding binding;
  private Preferencemanager preferencemanager;
  private String inviterToken=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityOutgoingInvitionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferencemanager=new Preferencemanager(getApplicationContext());


        String meetingType=getIntent().getStringExtra("type");
        UserModel user=(UserModel) getIntent().getSerializableExtra("user");
        if(meetingType!=null){
            if(meetingType.equals("video")){
                binding.imgMeetingType.setImageResource(R.drawable.baseline_videocam_24);
            }
        }
        if(user!=null){
            binding.textFirstChar.setText(user.name);
            binding.textEmail.setText(user.email);
        }
        binding.imageStopInvition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onBackPressed();
                if(user!=null){
                    cancelInvitation(user.token);
                }
            }
        });
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // sendFCMTokenToDatabase(task.getResult());
                        inviterToken=task.getResult();
                        initiateMeeting(meetingType, user.token);
                    } else {
                        // Handle error while fetching token
                        Toast.makeText(OutgoingInvitionActivity.this, "Failed to get FCM token", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void initiateMeeting(String meetingType,String receiverToken){
        try {
            JSONArray tokens=new JSONArray();
            tokens.put(receiverToken);

            JSONObject body=new JSONObject();
            JSONObject data=new JSONObject();

            data.put(Constraints.REMOTE_MSG_TYPE,Constraints.REMOTE_MSG_INVITATION);
            data.put(Constraints.REMOTE_MSG_MEETING_TYPE,meetingType);
            data.put(Constraints.KEY_NAME,preferencemanager.getString(Constraints.KEY_NAME));
            data.put(Constraints.KEY_EMAIL,preferencemanager.getString(Constraints.KEY_EMAIL));
            data.put(Constraints.REMOTE_MSG_INVITOR_TOKEN,inviterToken);
            preferencemanager.putString(Constraints.REMOTE_MSG_MEETING_TYPE,meetingType);
            preferencemanager.putString(Constraints.REMOTE_MSG_TYPE,Constraints.REMOTE_MSG_INVITATION);


            body.put(Constraints.REMOTE_MSG_DATA,data);
            body.put(Constraints.REMOTE_MSG_REGISTATION_IDS,tokens);

            sendRemodeMess(body.toString(),Constraints.REMOTE_MSG_INVITATION);
        }catch (Exception exception){
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void sendRemodeMess(String remoteMessBody,String type){
        ApiCilent.getClient().create(ApiService.class).sendRemoteMessage(
                Constraints.getRemoteMessageHeaders(),remoteMessBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call,@NonNull Response<String> response) {
              if(response.isSuccessful()){
                  if(type.equals(Constraints.REMOTE_MSG_INVITATION)){
                      Toast.makeText(OutgoingInvitionActivity.this, "Invation sent success", Toast.LENGTH_SHORT).show();
                  }
                  else if (type.equals(Constraints.REMOTE_MSG_INVITATION_REPONSE)){
                      Toast.makeText(OutgoingInvitionActivity.this, "Invation cancle", Toast.LENGTH_SHORT).show();
                       finish();
                  }
              }else {
                  Toast.makeText(OutgoingInvitionActivity.this, response.message(), Toast.LENGTH_SHORT).show();finish();
              }
            }

            @Override
            public void onFailure(@NonNull Call<String> call,@NonNull Throwable t) {
                Toast.makeText(OutgoingInvitionActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    private void cancelInvitation(String recevier){
        try {
            JSONArray tokens=new JSONArray();
            tokens.put(recevier);

            JSONObject body=new JSONObject();
            JSONObject data=new JSONObject();

            data.put(Constraints.REMOTE_MSG_TYPE,Constraints.REMOTE_MSG_INVITATION_REPONSE);
            data.put(Constraints.REMOTE_MSG_INVITATION_REPONSE,Constraints.REMOTE_MSG_INVITATION_CANCEL);
            preferencemanager.putString(Constraints.REMOTE_MSG_TYPE,Constraints.REMOTE_MSG_INVITATION_REPONSE);
            body.put(Constraints.REMOTE_MSG_DATA,data);
            body.put(Constraints.REMOTE_MSG_REGISTATION_IDS,tokens);

            sendRemodeMess(body.toString(),Constraints.REMOTE_MSG_INVITATION_CANCEL);
        }catch (Exception e){

        }
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constraints.REMOTE_MSG_INVITATION_REPONSE);
            if (type==Constraints.REMOTE_MSG_INVITATION_ACCEPTED) {
                Toast.makeText(context, "Accepts", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(context, "Rejects", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                broadcastReceiver,new IntentFilter(Constraints.REMOTE_MSG_INVITATION_REPONSE)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                broadcastReceiver
        );
    }
}