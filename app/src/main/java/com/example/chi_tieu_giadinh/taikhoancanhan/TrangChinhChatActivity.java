package com.example.chi_tieu_giadinh.taikhoancanhan;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;
import com.example.chi_tieu_giadinh.adapter.RecentConversionAdapter;
import com.example.chi_tieu_giadinh.databinding.ActivityTrangChinhChatBinding;
import com.example.chi_tieu_giadinh.interfaccee.ConversionListener;
import com.example.chi_tieu_giadinh.model.ChatMessageModel;
import com.example.chi_tieu_giadinh.model.UserModel;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Collections;

public class TrangChinhChatActivity extends AppCompatActivity implements ConversionListener {
    private ActivityTrangChinhChatBinding binding;
    private Preferencemanager preferencemanager;
    private ArrayList<ChatMessageModel> converssion;
    private RecentConversionAdapter recentConversionAdapter;
    private FirebaseFirestore databse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityTrangChinhChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        innit();
        loadUserDetails();
        setListeners();
        listemersConversaions();
    }
    //Code này được sử dụng để lắng nghe sự thay đổi trong một bộ sưu tập cụ thể trong Firestore
    //truy xuất các tài liệu phù hợp với các điều kiện nhất định
    private void listemersConversaions(){
        databse.collection(Constraints.KEY_COLECTION_CONVERSATIONS)
                .whereEqualTo(Constraints.KEY_SENDER_ID,preferencemanager.getString(Constraints.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        databse.collection(Constraints.KEY_COLECTION_CONVERSATIONS)
                .whereEqualTo(Constraints.KEY_RECEIVED_ID,preferencemanager.getString(Constraints.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }
    //Mã này định nghĩa một EventListener<QuerySnapshot> để xử lý các sự kiện thay đổi trong bộ sưu tập Firestore mà bạn đã đăng ký lắng nghe
    private final EventListener<QuerySnapshot> eventListener=((value, error) -> {
        if (error!=null){
            return;
        }
        if (value!=null){
            for (DocumentChange documentChange:value.getDocumentChanges()){
                //DocumentChange.Type.ADDED: Khi một tài liệu mới được thêm vào bộ sưu tập, mã này sẽ lấy thông tin từ tài liệu đó và tạo một đối tượng ChatMessage
                if(documentChange.getType()==DocumentChange.Type.ADDED){
                    String senderId=documentChange.getDocument().getString(Constraints.KEY_SENDER_ID);
                    String receveId=documentChange.getDocument().getString(Constraints.KEY_RECEIVED_ID);
                    ChatMessageModel chatMessage=new ChatMessageModel();
                    chatMessage.senderId=senderId;
                    chatMessage.receiverId=receveId;
                    if(preferencemanager.getString(Constraints.KEY_USER_ID).equals(senderId)){
                        chatMessage.conversionImge=documentChange.getDocument().getString(Constraints.KEY_RECEIVE_IMGE);
                        chatMessage.conversionname=documentChange.getDocument().getString(Constraints.KEY_RECEIVE_NAME);
                        chatMessage.conversionId=documentChange.getDocument().getString(Constraints.KEY_RECEIVED_ID);
                    }else {
                        chatMessage.conversionImge=documentChange.getDocument().getString(Constraints.KEY_SENDER_IMGE);
                        chatMessage.conversionname=documentChange.getDocument().getString(Constraints.KEY_SENDER_NAME);
                        chatMessage.conversionId=documentChange.getDocument().getString(Constraints.KEY_SENDER_ID);
                    }
                    chatMessage.message=documentChange.getDocument().getString(Constraints.KEY_LAST_MESSAGE);
                    chatMessage.dateOject=documentChange.getDocument().getDate(Constraints.KEY_TIMESTAMP);
                    converssion.add(chatMessage);
                }
                //Mã này được sử dụng để cập nhật thông tin khi một cuộc trò chuyện (conversation) đã bị xóa trong Firestore và đồng bộ hóa với
                // danh sách cuộc trò chuyện hiển thị trong giao diện người dùng.
                else if (documentChange.getType()==DocumentChange.Type.REMOVED) {
                    for (int i=0;i<converssion.size();i++){
                        String senderId=documentChange.getDocument().getString(Constraints.KEY_SENDER_ID);
                        String receiveID=documentChange.getDocument().getString(Constraints.KEY_RECEIVED_ID);
                        if(converssion.get(i).senderId.equals(senderId)&& converssion.get(i).senderId.equals(receiveID)){
                            converssion.get(i).message=documentChange.getDocument().getString(Constraints.KEY_LAST_MESSAGE);
                            converssion.get(i).dateOject=documentChange.getDocument().getDate(Constraints.KEY_TIMESTAMP);
                            break;
                        }
                    }
                }
            }
            Collections.sort(converssion,(obj1, obj2)->obj2.dateOject.compareTo(obj1.dateOject));
            recentConversionAdapter.notifyDataSetChanged();
            binding.conversionRecycview.smoothScrollToPosition(0);
            binding.conversionRecycview.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    });
    private void setListeners(){
        binding.fabnewChat.setOnClickListener(v->{
            startActivity(new Intent(TrangChinhChatActivity.this,UsersActivity.class));
        });
    }

    private void innit(){
        converssion=new ArrayList<>();
        recentConversionAdapter=new RecentConversionAdapter(converssion,this);
        binding.conversionRecycview.setAdapter(recentConversionAdapter);
        databse=FirebaseFirestore.getInstance();
    }
    private void loadUserDetails(){
        if (preferencemanager == null) {
            preferencemanager = new Preferencemanager(this);
        }
        binding.textName.setText(preferencemanager.getString(Constraints.KEY_NAME));
        String imageString = preferencemanager.getString(Constraints.KEY_IMGE);
        if (imageString != null) {
            //giải mã chuỗi thành mảng byte
            byte[] bytes= Base64.decode(imageString,Base64.DEFAULT);
            //Chuyển đổi mảng byte thành đối tượng Bitmap
            Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            binding.imgProfile.setImageBitmap(bitmap);
        }
    }
    @Override
    public void onConversionCliked(UserModel user) {
        Intent intent=new Intent(getApplicationContext(),ChatMessActivity.class);
        intent.putExtra(Constraints.KEY_USER,user);
        startActivity(intent);
    }

    @Override
    public void inittialVideomeeting(UserModel user) {
        if(user.token==null||user.token.trim().isEmpty()){
            Toast.makeText(this, "is not avalid for meeting", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Video meeting with", Toast.LENGTH_SHORT).show();
        }
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