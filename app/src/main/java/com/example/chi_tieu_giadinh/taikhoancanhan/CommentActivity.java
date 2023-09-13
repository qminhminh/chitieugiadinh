package com.example.chi_tieu_giadinh.taikhoancanhan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.chi_tieu_giadinh.adapter.CommentAdapter;
import com.example.chi_tieu_giadinh.databinding.ActivityCommentBinding;
import com.example.chi_tieu_giadinh.model.CommentModel;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CommentActivity extends AppCompatActivity implements CommentAdapter.Listen {
      ActivityCommentBinding binding;
      private Preferencemanager preferencemanager;
      ArrayList<CommentModel> arrayList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // để khởi tạo đối tượng binding từ layout được chỉ định (trong trường hợp này là layout của hoạt động CommentActivity).
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        //được sử dụng để thiết lập nội dung hiển thị của hoạt động là view gốc của layout đã được gắn kết.
        // Điều này có nghĩa là các phần tử trong layout được hiển thị trên màn hình khi hoạt động được khởi chạy.
        setContentView(binding.getRoot());
        int pos = getIntent().getIntExtra("pos",0); // Nhận vị trí từ Intent
        loadUserDetails();
        setListeners(pos);
        getListeners(pos);
        binding.rcycComment.setLayoutManager(new LinearLayoutManager(this));
    }
//phương thức này thực hiện truy vấn vào cơ sở dữ liệu Firestore để lấy các bình luận (comments) và
// hiển thị chúng trong một RecyclerView thông qua việc sử dụng một adapter.
// Các bình luận tại vị trí cụ thể (pos) được bỏ qua trong danh sách hiển thị.
private void getListeners(int pos) {
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference eventUsesCollection = firestore.collection(Constraints.KEY_COLECTION_ADD_TIN);
    CollectionReference eventUseRef = eventUsesCollection.document(String.valueOf(pos)).collection(Constraints.KEY_COLECTION_ADD_COMMENT);

    eventUseRef.get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                        // Kiểm tra vị trí của document

                            String name = documentSnapshot.getString(Constraints.KEY_COLECTION_ADD_COMMENT_NAME);
                            String img = documentSnapshot.getString(Constraints.KEY_COLECTION_ADD_COMMENT_IMAGE);
                            String comment = documentSnapshot.getString(Constraints.KEY_COLECTION_ADD_COMMENT_EDIT);
                            Date time = documentSnapshot.getDate(Constraints.KEY_COLECTION_ADD_COMMENT_DATE);
                            String id = documentSnapshot.getId();
                            String formattedTime = "";
                            if (time != null) {
                                formattedTime = getReadableDateTime(time);
                            }

                            CommentModel model = new CommentModel(id, img, name, comment, formattedTime);
                            arrayList.add(model);

                    }

                    CommentAdapter adapter = new CommentAdapter(arrayList,CommentActivity.this);
                    binding.rcycComment.setAdapter(adapter);
                }
            });
}

    //phương thức này thực hiện truy vấn vào cơ sở dữ liệu Firestore để lấy các bình luận (comments)
    private void setListeners(int pos) {
        binding.imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                CollectionReference eventUsesCollection = firestore.collection(Constraints.KEY_COLECTION_ADD_TIN);
                DocumentReference eventUseRef = eventUsesCollection.document(String.valueOf(pos)).collection(Constraints.KEY_COLECTION_ADD_COMMENT).document(uid);
                HashMap<String, Object> hashMap = new HashMap<>();
                preferencemanager.putString("posss", String.valueOf(pos));
                hashMap.put(Constraints.KEY_COLECTION_ADD_COMMENT_NAME, preferencemanager.getString(Constraints.KEY_NAME));
                hashMap.put(Constraints.KEY_COLECTION_ADD_COMMENT_IMAGE, preferencemanager.getString(Constraints.KEY_IMGE));
                hashMap.put(Constraints.KEY_COLECTION_ADD_COMMENT_EDIT, binding.tvComment.getText().toString());
                binding.tvComment.setText(null);
                eventUseRef.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });

            }
        });
    }

    private void loadUserDetails(){
        if (preferencemanager == null) {
            preferencemanager = new Preferencemanager(getApplicationContext());
        }
      //  binding.textName.setText(preferencemanager.getString(Constraints.KEY_NAME));
        String imageString = preferencemanager.getString(Constraints.KEY_IMGE);
        if (imageString != null) {
            byte[] bytes= Base64.decode(imageString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
           // binding.imgProfile.setImageBitmap(bitmap);
        }

    }
    ////Mã này có tác dụng chuyển đổi một đối tượng Date thành một chuỗi đại diện cho thời gian đọc được.
    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd,yyyy-hh:mm a", Locale.getDefault()).format(date);
    }

    @Override
    public void Comamt(CommentModel model, int position) {
        if (model.getName().equals(preferencemanager.getString(Constraints.KEY_NAME))){
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            CollectionReference eventUsesCollection = firestore.collection(Constraints.KEY_COLECTION_ADD_TIN);
            DocumentReference eventUseRef = eventUsesCollection.document(String.valueOf(position)).collection(Constraints.KEY_COLECTION_ADD_COMMENT).document(uid);
            eventUseRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(CommentActivity.this, "Thu hoi binh luan thanh cong", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}