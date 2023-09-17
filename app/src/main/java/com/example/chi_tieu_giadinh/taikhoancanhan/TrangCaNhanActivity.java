package com.example.chi_tieu_giadinh.taikhoancanhan;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;
import com.example.chi_tieu_giadinh.adapter.TrangCaNhanAdapter;
import com.example.chi_tieu_giadinh.adapter.TrangCaNhanDangTinAdapter;
import com.example.chi_tieu_giadinh.databinding.ActivityTrangCaNhanBinding;
import com.example.chi_tieu_giadinh.interfaccee.UserListeners;
import com.example.chi_tieu_giadinh.model.DangTinModel;
import com.example.chi_tieu_giadinh.model.UserModel;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TrangCaNhanActivity extends AppCompatActivity implements UserListeners {

    ActivityTrangCaNhanBinding binding;
    ArrayList<DangTinModel> arrayList;
    private Preferencemanager preferencemanager;
    private String encodedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityTrangCaNhanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferencemanager = new Preferencemanager(getApplicationContext());
        arrayList=new ArrayList<>();
        loadUserDetails();
        setCheckUser();
        getUsers();
        setListenser();
        add();

    }
    private void add(){
        binding.tvNameCt.setText(preferencemanager.getString(Constraints.KEY_NAME));
        binding.tvNgaysinh.setText(preferencemanager.getString(Constraints.KEY_NGAYTHANGNAM));
        binding.tvTuoi.setText(preferencemanager.getString(Constraints.KEY_DONGHO));
        binding.tvGioitinh.setText(preferencemanager.getString(Constraints.KEY_GIOITINH));
        binding.tvChucvu.setText(preferencemanager.getString(Constraints.KEY_CHUCVU));
        binding.tvNoio.setText(preferencemanager.getString(Constraints.KEY_NOIO));
        binding.tvEmail.setText(preferencemanager.getString(Constraints.KEY_EMAIL));
    }

    private void setListenser(){
        binding.imabuttonAnhnen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImg.launch(intent);
                Toast.makeText(TrangCaNhanActivity.this, "Đổi ảnh nền thành công", Toast.LENGTH_SHORT).show();
            }
        });
        binding.addTin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AddDangTinActivity.class));
            }
        });
        binding.createNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),CreateNoticeActivity.class));
            }
        });
    }
    private final ActivityResultLauncher<Intent> pickImg=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result->{
        if(result.getResultCode()==RESULT_OK){
            if(result.getData()!=null){
                Uri imgUri=result.getData().getData();
                try {
                    InputStream inputStream=getContentResolver().openInputStream(imgUri);
                    Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                    binding.anhnen.setImageBitmap(bitmap);
                    encodedImage=encodeImge(bitmap);
                    preferencemanager.putString(Constraints.KEY_IMGE_ANHNEN,encodedImage);
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
    private void getUsers() {

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constraints.KEY_USES_AUTH)
                .get()
                .addOnCompleteListener(task -> {
                    String curentUserId = preferencemanager.getString(Constraints.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        ArrayList<UserModel> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (curentUserId != null && curentUserId.equals(queryDocumentSnapshot.getId())) {
                                continue;
                            }
                            UserModel user = new UserModel();
                            user.name = queryDocumentSnapshot.getString(Constraints.KEY_NAME);
                            user.email = queryDocumentSnapshot.getString(Constraints.KEY_EMAIL);
                            user.image = queryDocumentSnapshot.getString(Constraints.KEY_IMGE);
                            user.token = queryDocumentSnapshot.getString(Constraints.KEY_FCM_TOKEN);
                            user.id = queryDocumentSnapshot.getId();
                            users.add(user);
                        }
                        if (users.size() > 0) {
                            TrangCaNhanAdapter userAdapter = new TrangCaNhanAdapter(users, this);
                            binding.recycViewBanBe.setAdapter(userAdapter);
                            binding.recycViewBanBe.setVisibility(View.VISIBLE);
                        } else {
                            showErrorMessgae();
                        }
                    } else {
                        showErrorMessgae();
                    }
                });
    }
    private void showErrorMessgae(){

    }

    private void setCheckUser(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference eventUsesCollectionadd = firestore.collection(Constraints.KEY_COLLECTIONS_ADDTIN_TRANGCN);
        CollectionReference eventUseRef = eventUsesCollectionadd;

                eventUseRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){


                   for ( QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        String ima_dai_dien = documentSnapshot.getString(Constraints.KEY_COLECTION_ADD_IMAGE_DAI_DIEN);
                        String img_dang_tin = documentSnapshot.getString(Constraints.KEY_COLECTION_ADD_IMAGE);
                        String eit = documentSnapshot.getString(Constraints.KEY_COLECTION_ADD_EDITTEXT);
                        String video = documentSnapshot.getString(Constraints.KEY_COLECTION_ADD_VIDEO);
                        String name = documentSnapshot.getString(Constraints.KEY_COLECTION_ADD_NAME);
                        Date time = documentSnapshot.getDate(Constraints.KEY_COLECTION_TIME);
                        String formattedTime = "";
                        if (time != null) {
                            formattedTime = getReadableDateTime(time);
                        }


                        long count = 0; // Giá trị mặc định
                        if (documentSnapshot.contains(Constraints.KEY_COLECTION_ADD_COUNT)) {
                            count = documentSnapshot.getLong(Constraints.KEY_COLECTION_ADD_COUNT);
                        }

                        String id = documentSnapshot.getId();
                       if(preferencemanager.getString(Constraints.KEY_NAME).equals(name)){
                           DangTinModel model = new DangTinModel(id, ima_dai_dien, img_dang_tin, video, name, eit, formattedTime, count);
                           arrayList.add(model);
                       }
                       else {

                       }
                    }

                TrangCaNhanDangTinAdapter adapter=new TrangCaNhanDangTinAdapter(arrayList,this);
                binding.recycViewDangTin.setAdapter(adapter);
            }
        });

    }

    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd,yyyy-hh:mm a", Locale.getDefault()).format(date);
    }
    private void loadUserDetails(){
        if (preferencemanager == null) {
            preferencemanager = new Preferencemanager(getApplicationContext());
        }
        binding.tvName.setText(preferencemanager.getString(Constraints.KEY_NAME));
        String imageString = preferencemanager.getString(Constraints.KEY_IMGE);
        String imgAnhnen=preferencemanager.getString(Constraints.KEY_IMGE_ANHNEN);
        if (imageString != null) {
            byte[] bytes= Base64.decode(imageString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            binding.imgProfile.setImageBitmap(bitmap);
        }
        if (imgAnhnen!=null) {
            byte[] bytes= Base64.decode(imgAnhnen, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            binding.anhnen.setImageBitmap(bitmap);
        }

    }

    @Override
    public void onUserClickd(UserModel user) {
        Intent intent=new Intent(getApplicationContext(),ChatMessActivity.class);
        intent.putExtra(Constraints.KEY_USER,user);
        startActivity(intent);
        finish();
    }

    @Override
    public void inittialVideomeeting(UserModel user) {

    }

    @Override
    public void inittialAudiomeeting(UserModel user) {

    }


}