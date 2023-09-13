package com.example.chi_tieu_giadinh.taikhoancanhan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chi_tieu_giadinh.MainActivity;
import com.example.chi_tieu_giadinh.databinding.ActivityStoryBinding;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class StoryActivity extends AppCompatActivity {
    private static final int REQUEST_PICK_VIDEO = 1;
    private ActivityStoryBinding binding;
    private Uri selectedVideoUri;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Preferencemanager preferencemanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        preferencemanager = new Preferencemanager(getApplicationContext());
        if(binding.edTextDang.getText().toString()!=null){
            setListenser();
        }
        else {
            Toast.makeText(this, "Hãy nhập gì đó đi :)_", Toast.LENGTH_SHORT).show();
        }

    }

    private void setListenser() {
        binding.addImag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGalleryForVideo();
            }
        });
        binding.buttonDangtin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Kiểm tra xem đã chọn video chưa
                if (selectedVideoUri != null) {
                    // Nếu đã chọn video, thực hiện tải lên Firebase và lưu vào Firestore
                    uploadVideoToFirebase(selectedVideoUri);
                } else {
                    // Nếu chưa chọn video, hiển thị thông báo cho người dùng
                    Toast.makeText(StoryActivity.this, "Vui lòng chọn video trước khi đăng tin!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StoryActivity.this, MainActivity.class));
            }
        });
    }

    private void openGalleryForVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_VIDEO && resultCode == RESULT_OK && data != null) {
            selectedVideoUri = data.getData();
            // Tại đây, bạn có thể tiến hành tải tệp video lên Firebase Storage.
            // Ví dụ:
            binding.ivVideo.setVideoURI(selectedVideoUri);
            binding.ivVideo.start(); // Bắt đầu phát video
        }
    }

    private void uploadVideoToFirebase(Uri videoUri) {
        StorageReference videoRef = storageRef.child("videos/" + System.currentTimeMillis() + ".mp4");
        videoRef.putFile(videoUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Lấy URL của tệp video từ Firebase Storage
                    videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Lưu URL tệp video vào Firestore
                        saveVideoUrlToFirestore(uri.toString());
                        Toast.makeText(StoryActivity.this, "Tải video lên thành công!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(StoryActivity.this, MainActivity.class));
                    });
                })
                .addOnFailureListener(e -> {
                    // Xử lý khi tải lên thất bại (thông báo hoặc ghi log lỗi, ...)
                    Toast.makeText(StoryActivity.this, "Tải video lên thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveVideoUrlToFirestore(String videoUrl) {
        // Tạo một tài liệu mới trong Firestore và lưu URL tệp video vào tài liệu này
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String authId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> videoData = new HashMap<>();
        videoData.put(Constraints.KEY_IMGE,preferencemanager.getString(Constraints.KEY_IMGE));
        videoData.put(Constraints.KEY_STORY_VIDEO, videoUrl);
        videoData.put(Constraints.KEY_STORY_DESCRPIT, binding.edTextDang.getText().toString());
        videoData.put(Constraints.KEY_NAME, preferencemanager.getString(Constraints.KEY_NAME));
        videoData.put("timestamp", FieldValue.serverTimestamp());
        db.collection(Constraints.KEY_COLLECTIONS_VIDEO_STRORY).document(authId)
                .set(videoData)
                .addOnSuccessListener(documentReference -> {
                    // Xử lý khi lưu URL thành công (thông báo hoặc cập nhật giao diện, ...)
                })
                .addOnFailureListener(e -> {
                    // Xử lý khi lưu URL thất bại (thông báo hoặc ghi log lỗi, ...)
                });
    }
}
