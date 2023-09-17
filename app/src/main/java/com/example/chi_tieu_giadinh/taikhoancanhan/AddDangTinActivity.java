package com.example.chi_tieu_giadinh.taikhoancanhan;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import com.example.chi_tieu_giadinh.cloudmessing.FcmNotificationsSender;
import com.example.chi_tieu_giadinh.databinding.ActivityAddDangTinBinding;
import com.example.chi_tieu_giadinh.fragment.HomeFragment;
import com.example.chi_tieu_giadinh.model.NoticeModel;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class AddDangTinActivity extends AppCompatActivity {

    private String encodedImage;
    private Uri imageUri;
    ArrayList<NoticeModel> arrayList = new ArrayList<>();
    ActivityAddDangTinBinding binding;
    private Preferencemanager preferencemanager;
    final  private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ImagesDangTin");
    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    FirebaseFirestore firebaseFirestore;
    private String enVideo;
    private static final String CHANNEL_ID = "my_channel";
    private static final CharSequence CHANNEL_NAME = "My Channel";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDangTinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseFirestore = FirebaseFirestore.getInstance();
        loadUserDetails();
        setLiseners();
    }

    private void setLiseners() {

        binding.buttonDangtin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadToFirebase(imageUri);
                //chexk anh dang

              sendNoticefication();
          }
        });
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            imageUri = data.getData();
                            binding.ivImag.setImageURI(imageUri);
                        } else {
                            //Toast.makeText(UploadActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        binding.addImag.setOnClickListener(view -> {
            Intent photoPicker = new Intent();
            photoPicker.setAction(Intent.ACTION_GET_CONTENT);
            photoPicker.setType("image/*");
            activityResultLauncher.launch(photoPicker);
        });

        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment homeFragment = new HomeFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(android.R.id.content, homeFragment);
                fragmentTransaction.commit();
                finish();
            }
        });
    }

    private void uploadToFirebase(Uri uri) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference eventUsesCollection = firestore.collection(Constraints.KEY_COLLECTIONS_CHECK_TIN);
        DocumentReference eventUseRef = eventUsesCollection.document(uid);
        final StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));

        imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String key = databaseReference.push().getKey();
                        databaseReference.child(key).setValue(uri.toString());
                        HashMap<Object,String> ad=new HashMap<>();
                        ad.put(Constraints.KEY_CHECK_IMG,preferencemanager.getString(Constraints.KEY_IMGE));
                        ad.put(Constraints.KEY_CHECK_DES,binding.edTextDang.getText().toString());
                        ad.put(Constraints.KEY_CHECK_NAME,preferencemanager.getString(Constraints.KEY_NAME));
                        ad.put(Constraints.KEY_CHECK_ANH_DANG,uri.toString());
                        ad.put(Constraints.KEY_EMAIL,preferencemanager.getString(Constraints.KEY_NAME));
                        ad.put(Constraints.KEY_CHECK_VIDEO,enVideo);

                        eventUseRef.set(ad)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(AddDangTinActivity.this, "Suceess", Toast.LENGTH_SHORT).show();
                                        Fragment homeFragment = new HomeFragment();
                                        FragmentManager fragmentManager = getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(android.R.id.content, homeFragment);
                                        fragmentTransaction.commit();
                                        finish();

                                    }
                                });
                    }
                });
            }
        });
    }
    private String getFileExtension(Uri fileUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
//thông báo
    private void sendNoticefication() {
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        FcmNotificationsSender notificationsSender=new FcmNotificationsSender("/topics/all",
                preferencemanager.getString(Constraints.KEY_NAME),
                "Đã đang tin mới",getApplicationContext(), AddDangTinActivity.this);
        notificationsSender.SendNotifications();
    }

    private void loadUserDetails(){
        if (preferencemanager == null) {
            preferencemanager = new Preferencemanager(getApplicationContext());
        }
        binding.textName.setText(preferencemanager.getString(Constraints.KEY_NAME));
        String imageString = preferencemanager.getString(Constraints.KEY_IMGE);
        if (imageString != null) {
            byte[] bytes= Base64.decode(imageString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            binding.imgProfile.setImageBitmap(bitmap);
        }

    }
// xử lý kết quả trả về từ hộp thoại chọn tệp.
    private final ActivityResultLauncher<Intent> pickImg = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        //Kiểm tra xem kết quả trả về có thành công không
        if (result.getResultCode() == RESULT_OK) {
            //Kiểm tra xem có dữ liệu được trả về không
            if (result.getData() != null) {
                //Lấy URI của phương thức getData().
                Uri mediaUri = result.getData().getData();
                //Xác định loại phương tiện được chọn (image/ hoặc video/).
                if (mediaUri != null) {
                    String mediaType = getContentResolver().getType(mediaUri);
                    if (mediaType != null) {
                        //Nếu là ảnh, mở InputStream từ URI và chuyển đổi thành Bitmap.
                        //Hiển thị ảnh trong ImageView và mã hóa thành chuỗi để lưu trữ
                        if (mediaType.startsWith("image/")) {
                            // Xử lý ảnh
                            try {
                                InputStream inputStream = getContentResolver().openInputStream(mediaUri);
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                binding.ivImag.setVisibility(View.VISIBLE);
                                binding.ivVideo.setVisibility(View.GONE);
                                binding.ivImag.setImageBitmap(bitmap);
                                encodedImage = encodeImge(bitmap);
                            } catch (FileNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        //Nếu là video, hiển thị video trong VideoView và lưu URI của video
                        else if (mediaType.startsWith("video/")) {
                            // Xử lý video
                            binding.ivImag.setVisibility(View.GONE);
                            binding.ivVideo.setVisibility(View.VISIBLE);
                            binding.ivVideo.setVideoURI(mediaUri);
                            binding.ivVideo.start();
                            enVideo=mediaUri.toString();
                            // Thực hiện các xử lý tương ứng với video được chọn
                        }
                    }
                }

            }
        }
    });

    //được sử dụng để mã hóa một đối tượng Bitmap thành một chuỗi được mã hóa dưới dạng Base64.
    private String encodeImge(Bitmap bitmap){
        //Đầu tiên, định nghĩa các kích thước cho ảnh xem trước (previewWidth và previewHeigth). Kích thước xem trước được tính dựa trên tỷ lệ khung hình của ảnh gốc.
        int previewWidth=150;
        int previewHeigth=bitmap.getHeight()+previewWidth/bitmap.getWidth();
        // tạo một ảnh xem trước với kích thước được chỉ định.
        Bitmap previewBitmap=Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeigth,false);
       //để lưu trữ dữ liệu của ảnh xem trước.
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
       //Nén ảnh xem trước vào đối tượng ByteArrayOutputStream với định dạng JPEG và mức nén là 50.
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes=byteArrayOutputStream.toByteArray();
        //ể mã hóa mảng byte thành một chuỗi dạng Base64.
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

}