package com.example.chi_tieu_giadinh.taikhoancanhan;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.databinding.ActivitySignUpBinding;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    private Preferencemanager preferencemanager;
    private String encodedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        preferencemanager=new Preferencemanager(getApplicationContext());
        setListeners();

    }

    private void setListeners(){
        binding.layoutImage.setOnClickListener(view -> {
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImg.launch(intent);
        });
        binding.loginRedirectText.setOnClickListener(view -> onBackPressed());
        binding.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidSignUpDetails()){
                    signUp();
                }
            }
        });
    }
    private void signUp(){
        String email=binding.signupEmail.getText().toString().trim();
        String password=binding.signupPassword.getText().toString();
        //ể tạo một tài khoản người dùng mới trên Firebase Authentication.
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(new Intent(getApplicationContext(),SignInActivity.class));
                        Toast.makeText(SignUpActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        FirebaseFirestore database=FirebaseFirestore.getInstance();
//==============================================================================
                        FirebaseUser userAu=firebaseAuth.getCurrentUser();
                        DocumentReference df=database.collection(Constraints.KEY_USES_AUTH).document(userAu.getUid());
                        Map<String,Object> userInfo=new HashMap<>();
                        userInfo.put(Constraints.KEY_NAME,binding.signupHoVaTen.getText().toString());
                        userInfo.put(Constraints.KEY_DONGHO,binding.signupDongHoBen.getText().toString());
                        userInfo.put(Constraints.KEY_CHUCVU,binding.signupChucVu.getText().toString());
                        userInfo.put(Constraints.KEY_NGAYTHANGNAM,binding.signupNamSinh.getText().toString());
                        userInfo.put(Constraints.KEY_NOIO,binding.signupNoiO.getText().toString());
                        userInfo.put(Constraints.KEY_GIOITINH,binding.signupGioiTinh.getText().toString());
                        userInfo.put(Constraints.KEY_EMAIL,binding.signupEmail.getText().toString());
                        userInfo.put(Constraints.KEY_PASSWORD,binding.signupPassword.getText().toString());
                        userInfo.put(Constraints.KEY_IMGE,encodedImage);
                        userInfo.put(Constraints.KEY_IS_USERS,"1");
                        df.set(userInfo).addOnSuccessListener(documentReference -> {
                                    preferencemanager.putBoolean(Constraints.KEY_IS_SIGN_IN,true);
                                    preferencemanager.putString(Constraints.KEY_USER_ID, df.getId());
                                    preferencemanager.putString(Constraints.KEY_NAME,binding.signupHoVaTen.getText().toString());
                                    preferencemanager.putString(Constraints.KEY_GIOITINH,binding.signupGioiTinh.getText().toString());
                                    preferencemanager.putString(Constraints.KEY_DONGHO,binding.signupDongHoBen.getText().toString());
                                    preferencemanager.putString(Constraints.KEY_IMGE,encodedImage);
                                    preferencemanager.putString(Constraints.KEY_NOIO,binding.signupNoiO.getText().toString());
                                    preferencemanager.putString(Constraints.KEY_CHUCVU,binding.signupChucVu.getText().toString());
                                    preferencemanager.putString(Constraints.KEY_NGAYTHANGNAM,binding.signupNamSinh.getText().toString());
                                    Intent intent=new Intent(getApplicationContext(), SignInActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(v->{

                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, "Đăng ký không thành công", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private Boolean isValidSignUpDetails(){
        if(binding.signupHoVaTen.getText().toString().trim().isEmpty()){
            showToast("Tên không được để trống");return false;
        } else if (binding.signupDongHoBen.getText().toString().trim().isEmpty()) {
            showToast("Dòng họ không được để trống");return false;
        } else if (binding.signupGioiTinh.getText().toString().trim().isEmpty()) {
            showToast("Giới tính không được để trống");return false;
        } else if (binding.signupEmail.getText().toString().trim().isEmpty()) {
            showToast("Email không được để trống");return false;
        } else if (binding.signupPassword.getText().toString().trim().isEmpty()) {
            showToast("Mật khẩu không được để trống");return false;
        } else if (encodedImage==null) {
            showToast("Hãy thêm ảnh vào");return false;
        } else if (binding.signupChucVu.getText().toString().trim().isEmpty()) {
            showToast("Hãy thêm chức vụ vào");return false;
        } else if (binding.signupNamSinh.getText().toString().trim().isEmpty()) {
            showToast("Hãy thêm năm sinh vào");return false;
        } else if (binding.signupNoiO.getText().toString().trim().isEmpty()) {
            showToast("Hãy thêm nơi ở vào");return false;
        } else {
            return true;
        }
    }
    private void showToast(String a){
        Toast.makeText(this, a, Toast.LENGTH_SHORT).show();
    }
    private final ActivityResultLauncher<Intent> pickImg=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result->{
        if(result.getResultCode()==RESULT_OK){
            if(result.getData()!=null){
                Uri imgUri=result.getData().getData();
                try {
                    // đọc dữ liệu
                    InputStream inputStream=getContentResolver().openInputStream(imgUri);
                    Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                    //c huyển đổi sang bitmap
                    binding.imgProfile.setImageBitmap(bitmap);
                    binding.textAddImage.setVisibility(View.GONE);
                    encodedImage=encodeImge(bitmap);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    });
    private String encodeImge(Bitmap bitmap){
        int previewWidth=150;
        int previewHeigth=bitmap.getHeight()+previewWidth/bitmap.getWidth();
        Bitmap previewBitmap=Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeigth,false);
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}