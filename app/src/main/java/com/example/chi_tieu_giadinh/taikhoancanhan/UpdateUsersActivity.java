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

import com.example.chi_tieu_giadinh.MainActivity;
import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.databinding.ActivityUpdateUsersBinding;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateUsersActivity extends AppCompatActivity {
        ActivityUpdateUsersBinding binding;
    private GoogleMap googleMap;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    private Preferencemanager preferencemanager;
    private String encodedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUpdateUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        preferencemanager=new Preferencemanager(getApplicationContext());
        data();
        setListeners();
    }
    private void data(){
        binding.signupHoVaTen.setText(preferencemanager.getString(Constraints.KEY_NAME));
        binding.signupGioiTinh.setText(preferencemanager.getString(Constraints.KEY_GIOITINH));
        binding.signupDongHoBen.setText(preferencemanager.getString(Constraints.KEY_DONGHO));
        binding.signupNoiO.setText(preferencemanager.getString(Constraints.KEY_NOIO));
        binding.signupChucVu.setText(preferencemanager.getString(Constraints.KEY_CHUCVU));
        binding.signupNamSinh.setText(preferencemanager.getString(Constraints.KEY_NGAYTHANGNAM));
        binding.signupEmail.setText(preferencemanager.getString(Constraints.KEY_EMAIL));
        binding.signupPassword.setText(preferencemanager.getString(Constraints.KEY_PASSWORD));
        binding.imgProfile.setImageBitmap(getUserImage(preferencemanager.getString(Constraints.KEY_IMGE)));

    }
    private Bitmap getUserImage(String encodedImage){
        if (encodedImage == null) {
            // Handle the case when the encoded image is null
            return null;
        }
        byte[] bytes= Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
    private void setListeners(){
        binding.layoutImage.setOnClickListener(view -> {
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImg.launch(intent);
        });

        binding.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidSignUpDetails()){
                    signUp();
                }
            }
        });
    }

    private void signUp() {
        // ...
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        FirebaseUser userAuth = firebaseAuth.getCurrentUser();
        DocumentReference df = database.collection(Constraints.KEY_USES_AUTH).document(userAuth.getUid());
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Lấy thông tin người dùng từ các trường nhập liệu trên giao diện
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put(Constraints.KEY_NAME, binding.signupHoVaTen.getText().toString());
        userInfo.put(Constraints.KEY_DONGHO, binding.signupDongHoBen.getText().toString());
        userInfo.put(Constraints.KEY_CHUCVU, binding.signupChucVu.getText().toString());
        userInfo.put(Constraints.KEY_NGAYTHANGNAM, binding.signupNamSinh.getText().toString());
        userInfo.put(Constraints.KEY_NOIO, binding.signupNoiO.getText().toString());
        userInfo.put(Constraints.KEY_GIOITINH, binding.signupGioiTinh.getText().toString());
        userInfo.put(Constraints.KEY_EMAIL, binding.signupEmail.getText().toString());
        userInfo.put(Constraints.KEY_IMGE, encodedImage);
        userInfo.put(Constraints.KEY_IS_USERS, "1");

        df.update(userInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Cập nhật thông tin người dùng trong Firestore thành công
                        Toast.makeText(getApplicationContext(), "Cập nhật thông tin người dùng thành công", Toast.LENGTH_SHORT).show();

                        // Cập nhật email và mật khẩu trong Firebase Authentication
                        String newEmail = binding.signupEmail.getText().toString();
                        String newPassword = binding.signupPassword.getText().toString();

                        if (userAuth != null) {
                            // Cập nhật email
                            userAuth.updateEmail(newEmail)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Cập nhật email thành công
                                            Toast.makeText(getApplicationContext(), "Email đã được cập nhật thành công", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(UpdateUsersActivity.this,MainActivity.class));finish();
                                            preferencemanager.putString(Constraints.KEY_NAME,binding.signupHoVaTen.getText().toString());
                                            preferencemanager.putString(Constraints.KEY_IMGE,encodedImage);
                                            preferencemanager.putString(Constraints.KEY_NOIO,binding.signupNoiO.getText().toString());
                                            preferencemanager.putString(Constraints.KEY_CHUCVU,binding.signupChucVu.getText().toString());
                                            preferencemanager.putString(Constraints.KEY_NGAYTHANGNAM,binding.signupNamSinh.getText().toString());
                                            preferencemanager.putString(Constraints.KEY_GIOITINH,binding.signupGioiTinh.getText().toString());
                                            preferencemanager.putString(Constraints.KEY_DONGHO,binding.signupDongHoBen.getText().toString());
                                            // Cập nhật mật khẩu
                                            userAuth.updatePassword(newPassword)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // Cập nhật mật khẩu thành công
                                                            //String address =preferencemanager.getString(Constraints.KEY_NOIO);

                                                            String name=preferencemanager.getString(Constraints.KEY_NAME);
                                                            Geocoder geocoder = new Geocoder(UpdateUsersActivity.this);
                                                            try {
                                                                List<Address> addresses = geocoder.getFromLocationName(binding.signupNoiO.getText().toString(), 1);
                                                                if (!addresses.isEmpty()) {
                                                                    Address firstAddress = addresses.get(0);
                                                                    double latitude = firstAddress.getLatitude();
                                                                    double longitude = firstAddress.getLongitude();


                                                                    DocumentReference dff = database.collection(Constraints.KEY_COLLECTIONS_LOCATION).document(userAuth.getUid());
                                                                    Map<String, Object> locationData = new HashMap<>();
                                                                    locationData.put(Constraints.KEY_LOCATION_ADDRESS, binding.signupNoiO.getText().toString());
                                                                    locationData.put(Constraints.KEY_LOCATION_LATITIUE, latitude);
                                                                    locationData.put(Constraints.KEY_LOCATION_LONGTITUDE, longitude);
                                                                    locationData.put(Constraints.KEY_NAME,preferencemanager.getString(Constraints.KEY_NAME));

                                                                    dff.collection(Constraints.KEY_COLLECTIONS_LOCATION).document(uid)
                                                                            .update(locationData)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    Toast.makeText(UpdateUsersActivity.this, "Update thanh cong địa chỉ.", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    Toast.makeText(UpdateUsersActivity.this, "Update ko thanh cong địa chỉ.", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });

                                                                } else {
                                                                    Toast.makeText(UpdateUsersActivity.this, "Không tìm thấy địa chỉ.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }



                                                            Toast.makeText(getApplicationContext(), "Mật khẩu đã được cập nhật thành công", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Cập nhật mật khẩu thất bại
                                                            Toast.makeText(getApplicationContext(), "Cập nhật mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Cập nhật email thất bại
                                            Toast.makeText(getApplicationContext(), "Cập nhật email thất bại", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Cập nhật thông tin người dùng trong Firestore thất bại
                        Toast.makeText(getApplicationContext(), "Cập nhật thông tin người dùng thất bại", Toast.LENGTH_SHORT).show();
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
            return  true;
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
                    InputStream inputStream=getContentResolver().openInputStream(imgUri);
                    Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
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