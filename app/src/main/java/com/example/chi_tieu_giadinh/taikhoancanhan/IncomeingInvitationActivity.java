package com.example.chi_tieu_giadinh.taikhoancanhan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.databinding.ActivityIncomeingInvitationBinding;
import com.example.chi_tieu_giadinh.network.ApiCilent;
import com.example.chi_tieu_giadinh.network.ApiService;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import org.json.JSONArray;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomeingInvitationActivity extends AppCompatActivity {

    private ActivityIncomeingInvitationBinding binding;
    private Preferencemanager preferencemanager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityIncomeingInvitationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferencemanager=new Preferencemanager(getApplicationContext());

        String meetingType=getIntent().getStringExtra(Constraints.REMOTE_MSG_MEETING_TYPE);
        if(meetingType!=null){
            if(meetingType.equals("video")){
                binding.imgMeetingType.setImageResource(R.drawable.baseline_videocam_24);
            }
        }
        String name=getIntent().getStringExtra(Constraints.KEY_NAME);
        if(name!=null){
            binding.textFirstChar.setText(name);
        }
        binding.textUsername.setText(name);
        binding.textEmail.setText(getIntent().getStringExtra(Constraints.KEY_EMAIL));
        binding.imageRejectInvition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // onBackPressed();
                sendInvitationReponse(Constraints.REMOTE_MSG_INVITATION_REJECTED,getIntent().getStringExtra(Constraints.REMOTE_MSG_INVITOR_TOKEN));
            }
        });
        binding.imageAcceptInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendInvitationReponse(Constraints.REMOTE_MSG_INVITATION_ACCEPTED,getIntent().getStringExtra(Constraints.REMOTE_MSG_INVITOR_TOKEN));
            }
        });
    }


    private void sendInvitationReponse(String type,String recevier){
try {
    JSONArray tokens=new JSONArray();
    tokens.put(recevier);

    JSONObject body=new JSONObject();
    JSONObject data=new JSONObject();

    data.put(Constraints.REMOTE_MSG_TYPE,Constraints.REMOTE_MSG_INVITATION_REPONSE);
    data.put(Constraints.REMOTE_MSG_INVITATION_REPONSE,type);
   preferencemanager.putString(Constraints.REMOTE_MSG_TYPE,Constraints.REMOTE_MSG_INVITATION_REPONSE);
   preferencemanager.putString(Constraints.REMOTE_MSG_INVITATION_REPONSE,type);
    body.put(Constraints.REMOTE_MSG_DATA,data);
    body.put(Constraints.REMOTE_MSG_REGISTATION_IDS,tokens);

    sendRemodeMess(body.toString(),type);
}catch (Exception e){

}
    }


    private void sendRemodeMess(String remoteMessBody,String type){
        ApiCilent.getClient().create(ApiService.class).sendRemoteMessage(
                Constraints.getRemoteMessageHeaders(),remoteMessBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if(response.isSuccessful()){
                   if(type.equals(Constraints.REMOTE_MSG_INVITATION_ACCEPTED)){
                       Toast.makeText(IncomeingInvitationActivity.this, "invitation accept ", Toast.LENGTH_SHORT).show();
                   }
                   else {
                       Toast.makeText(IncomeingInvitationActivity.this, "invitation no  ", Toast.LENGTH_SHORT).show();
                   }
                }else {
                    Toast.makeText(IncomeingInvitationActivity.this, response.message(), Toast.LENGTH_SHORT).show();finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call,@NonNull Throwable t) {
                Toast.makeText(IncomeingInvitationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}