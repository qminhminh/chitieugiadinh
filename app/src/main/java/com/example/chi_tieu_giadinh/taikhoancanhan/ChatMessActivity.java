package com.example.chi_tieu_giadinh.taikhoancanhan;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import com.example.chi_tieu_giadinh.adapter.ChatAdapter;
import com.example.chi_tieu_giadinh.databinding.ActivityChatMessBinding;
import com.example.chi_tieu_giadinh.model.ChatMessageModel;
import com.example.chi_tieu_giadinh.model.UserModel;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class ChatMessActivity extends AppCompatActivity {
    private ActivityChatMessBinding binding;
    private UserModel receiverUser;
    private ArrayList<ChatMessageModel> chatMessages;
    private ChatAdapter chatAdapter;
    private Preferencemanager preferencemanager;
    private FirebaseFirestore databse;
    private String converaionId=null;
    private Boolean isReceivedAvailiable=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatMessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListenners();//1
        loadReaciveDeatails();//2
        init();//3
        litenserMessage();//4
    }
    //Mã này có tác dụng lắng nghe sự thay đổi trong bộ sưu tập "chat" trên Firestore
    //truy vấn các tin nhắn giữa người gửi và người nhận để hiển thị trong giao diện người dùng
    private void litenserMessage(){
        databse.collection(Constraints.KEY_COLECTION_CHAT)
                .whereEqualTo(Constraints.KEY_SENDER_ID,preferencemanager.getString(Constraints.KEY_USER_ID))
                .whereEqualTo(Constraints.KEY_RECEIVED_ID,receiverUser.id)
                .addSnapshotListener(eventListener);
        databse.collection(Constraints.KEY_COLECTION_CHAT)
                .whereEqualTo(Constraints.KEY_SENDER_ID,receiverUser.id)
                .whereEqualTo(Constraints.KEY_RECEIVED_ID,preferencemanager.getString(Constraints.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }
    private final EventListener<QuerySnapshot> eventListener=(value, error)->{
        if(error!=null){
            return;
        }if(value!=null){
            int count=chatMessages.size();
            for (DocumentChange documentChange: value.getDocumentChanges()){
                if(documentChange.getType()==DocumentChange.Type.ADDED){
                    ChatMessageModel chatMessage=new ChatMessageModel();
                    chatMessage.senderId=documentChange.getDocument().getString(Constraints.KEY_SENDER_ID);
                    chatMessage.receiverId=documentChange.getDocument().getString(Constraints.KEY_RECEIVED_ID);
                    chatMessage.message=documentChange.getDocument().getString(Constraints.KEY_MESSAGE);
                    chatMessage.dateTime=getReadableDateTime(documentChange.getDocument().getDate(Constraints.KEY_TIMESTAMP));
                    chatMessage.dateOject=documentChange.getDocument().getDate(Constraints.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages,(obj1, obj2)->obj1.dateOject.compareTo(obj2.dateOject));
            if(count==0){
                chatAdapter.notifyDataSetChanged();
            }else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size(),chatMessages.size());
                binding.chatRecycview.smoothScrollToPosition(chatMessages.size()-1);
            }
            binding.chatRecycview.setVisibility(View.VISIBLE);
        }
        binding.chatRecycview.setVisibility(View.VISIBLE);
        if(converaionId==null){
            checkForConversion();
        }
    };
    //Kiểm tra xem có tin nhắn nào trong chatMessages hay không. Nếu có,
    // thực hiện kiểm tra cuộc trò chuyện cho cả người gửi và người nhận.
    private void checkForConversion(){
        if(chatMessages.size()!=0){
            checkForConversaionRemotely(preferencemanager.getString(Constraints.KEY_USER_ID),receiverUser.id);
            checkForConversaionRemotely(receiverUser.id,preferencemanager.getString(Constraints.KEY_USER_ID));
        }
    }
    //Kiểm tra sự tồn tại của cuộc trò chuyện giữa người gửi
    // và người nhận trong bộ sưu tập "conversations" trên Firestore.
    private void checkForConversaionRemotely(String senderId,String receuveId){
      //chỉ lấy các cuộc trò chuyện được gửi bởi người gửi
        databse.collection(Constraints.KEY_COLECTION_CONVERSATIONS)
                .whereEqualTo(Constraints.KEY_SENDER_ID,senderId)
                .whereEqualTo(Constraints.KEY_RECEIVED_ID,receuveId)
                .get()
                .addOnCompleteListener(conversionCompeteListener);
    }
    //Xử lý kết quả truy vấn và lấy ID của cuộc trò chuyện nếu kết quả truy vấn thành công
    // và có ít nhất một tài liệu cuộc trò chuyện được tìm thấy.
    private final OnCompleteListener<QuerySnapshot> conversionCompeteListener= task->{
      //  Kiểm tra xem task thành công, kết quả không rỗng và có ít nhất một tài liệu cuộc trò chuyện được tìm thấy.
        if(task.isSuccessful()&&task.getResult()!=null&&task.getResult().getDocuments().size()>0){
            //Lấy tài liệu cuộc trò chuyện đầu tiên từ kết quả và lấy ID của nó.
            DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
            //Gán giá trị converaionId bằng ID của cuộc trò chuyện.
            converaionId=documentSnapshot.getId();
        }
    };
    //Mã này có tác dụng chuyển đổi một đối tượng Date thành một chuỗi đại diện cho thời gian đọc được.
    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd,yyyy-hh:mm a", Locale.getDefault()).format(date);
    }
    private void init(){
        preferencemanager=new Preferencemanager(getApplicationContext());
        chatMessages=new ArrayList<>();
        chatAdapter=new ChatAdapter(chatMessages,getBitmapFromEncoded(receiverUser.image),preferencemanager.getString(Constraints.KEY_USER_ID));
        binding.chatRecycview.setAdapter(chatAdapter);
        databse=FirebaseFirestore.getInstance();
    }
    private Bitmap getBitmapFromEncoded(String encodeedImge){
        byte[] bytes= Base64.decode(encodeedImge,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
   // Phương thức này được gọi để tải thông tin người dùng nhận và hiển thị tên của người dùng nhận lên giao diện.
    private void loadReaciveDeatails(){
        //Lấy đối tượng người dùng nhận từ intent, sử dụng getSerializableExtra() để nhận đối tượng người dùng từ khóa Constraints.KEY_USER.
        receiverUser= (UserModel) getIntent().getSerializableExtra(Constraints.KEY_USER);
       //receiverUser.name là tên của người dùng nhận được lưu trữ trong đối tượng receiverUser.
        binding.textName.setText(receiverUser.name);
    }
    private void setListenners(){
        binding.imgBack.setOnClickListener(v-> onBackPressed());
        binding.layouSend.setOnClickListener(v->sendMessage());
    }
    //Mã này có tác dụng gửi tin nhắn từ người dùng hiện tại đến người nhận.
    private void sendMessage(){
        HashMap<String,Object> messgae=new HashMap<>();
        messgae.put(Constraints.KEY_SENDER_ID,preferencemanager.getString(Constraints.KEY_USER_ID));
     if(receiverUser.id!=null){
         messgae.put(Constraints.KEY_RECEIVED_ID,receiverUser.id);
     }else {
         messgae.put(Constraints.KEY_RECEIVED_ID,preferencemanager.getString(Constraints.KEY_USER_ID));
     }
        messgae.put(Constraints.KEY_MESSAGE,binding.inputMessage.getText().toString());
        messgae.put(Constraints.KEY_TIMESTAMP,new Date());
        //Thêm tin nhắn vào bộ sưu tập "chat" tao
        databse.collection(Constraints.KEY_COLECTION_CHAT).add(messgae);
        //Kiểm tra nếu đã tồn tại ID cuộc trò chuyện
        if(converaionId!=null){
           // thực hiện cập nhật cuộc trò chuyện.
                    updateConversion(binding.inputMessage.getText().toString());
        }
        //Ngược lại, tạo một cuộc trò chuyện mới.
        else {
            HashMap<String,Object> conversion=new HashMap<>();
            conversion.put(Constraints.KEY_SENDER_ID,preferencemanager.getString(Constraints.KEY_USER_ID));
            conversion.put(Constraints.KEY_SENDER_NAME,preferencemanager.getString(Constraints.KEY_NAME));
            conversion.put(Constraints.KEY_SENDER_IMGE,preferencemanager.getString(Constraints.KEY_IMGE));
            conversion.put(Constraints.KEY_RECEIVED_ID,receiverUser.id);
            conversion.put(Constraints.KEY_RECEIVE_NAME,receiverUser.name);
            conversion.put(Constraints.KEY_RECEIVE_IMGE,receiverUser.image);
            conversion.put(Constraints.KEY_LAST_MESSAGE,binding.inputMessage.getText().toString());
            conversion.put(Constraints.KEY_TIMESTAMP,new Date());
            addConversion(conversion);
        }
        //Xóa nội dung tin nhắn trong trường nhập liệu sau khi gửi tin nhắn thành công.
        binding.inputMessage.setText(null);
    }
    //Mã này có tác dụng cập nhật cuộc trò chuyện hiện tại
    private void updateConversion(String messgae){
        //Tạo một tham chiếu đến tài liệu cuộc trò chuyện
        // trong bộ sưu tập "conversations" trên Firestore bằng cách sử dụng ID của cuộc trò chuyện (converaionId).
        DocumentReference documentReference=
                databse.collection(Constraints.KEY_COLECTION_CONVERSATIONS)
                        .document(converaionId);
        //Cập nhật nội dung tin nhắn mới (messgae) vào tài liệu cuộc trò chuyện.
        //Cập nhật thời gian cập nhật cuộc trò chuyện thành thời gian hiện tại.
        documentReference.update(Constraints.KEY_LAST_MESSAGE,messgae,Constraints.KEY_TIMESTAMP,new Date());
    }
    // thêm một cuộc trò chuyện mới vào cơ sở dữ liệu Firestore
    private void addConversion(HashMap<String,Object> conversion){
        //Thêm đối tượng cuộc trò chuyện (conversion) vào bộ sưu tập "conversations" trên Firestore.
        //Nếu thêm cuộc trò chuyện thành công, lấy ID của tài liệu cuộc trò chuyện được thêm vào và gán cho biến converaionId.
        databse.collection(Constraints.KEY_COLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> converaionId= documentReference.getId());

    }
    //onResume(): Đây là phương thức ghi đè từ lớp cha Activity và được gọi khi hoạt động
    // ChatMessActivity tiếp tục hoạt động sau khi ngừng tạm thờ
    @Override
    protected void onResume() {
        super.onResume();
        listenserAvailableOfReceive();
    }
    // Phương thức này được gọi để lắng nghe sự khả dụng của người nhận thông qua việc theo dõi tài liệu
    // của người nhận trong bộ sưu tập "conversations" trên Firestore.
    private void listenserAvailableOfReceive(){
        //Đăng ký một trình nghe (listener) theo dõi thay đổi của tài liệu người nhận trên Firestore.
        databse.collection(Constraints.KEY_COLECTION_CONVERSATIONS)
                .document(receiverUser.id)
                //Trình nghe được triển khai dưới dạng một biểu thức lambda để xử lý giá trị tài liệu trả về và lỗi (nếu có).
                .addSnapshotListener(ChatMessActivity.this,((value, error) -> {
                    if ((error!=null)){
                        return;
                    }
                    if (value!=null){
                        //Nếu giá trị tài liệu trả về có thuộc tính số lượng tồn tại trong tài liệu
                        //chuyển đổi giá trị này thành một số nguyên (availiable) và cập nhật
                        // isReceivedAvailiable thành true nếu availiable bằng 1, ngược lại thành false.
                        if(value.getLong(Constraints.KEY_AVALIABLE)!=null){
                            int availiable= Objects.requireNonNull(value.getLong(Constraints.KEY_AVALIABLE)).intValue();
                            isReceivedAvailiable=availiable==1;
                        }
                        //Lấy giá trị của thuộc tính "KEY_FCM_TOKEN" trong tài liệu và cập nhật receiverUser.token thành giá trị này.
                        receiverUser.token=value.getString(Constraints.KEY_FCM_TOKEN);
                    }
                }));
    }
}