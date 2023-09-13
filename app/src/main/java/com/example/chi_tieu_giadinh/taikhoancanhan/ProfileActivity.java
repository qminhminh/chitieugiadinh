package com.example.chi_tieu_giadinh.taikhoancanhan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.adapter.TrangCaNhanAdapter;
import com.example.chi_tieu_giadinh.adapter.TrangCaNhanDangTinAdapter;
import com.example.chi_tieu_giadinh.databinding.ActivityProfileBinding;
import com.example.chi_tieu_giadinh.interfaccee.UserListeners;
import com.example.chi_tieu_giadinh.model.DangTinModel;
import com.example.chi_tieu_giadinh.model.UserModel;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity implements UserListeners{
    Bundle bundle;
    private RecyclerView recyclerViewBanBe;
    ArrayList<DangTinModel> arrayList;
    private  RecyclerView recyclerViewBaiDang;
    private Preferencemanager preferencemanager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        preferencemanager=new Preferencemanager(getApplicationContext());
        setListeners();


    }
    private void setListeners(){

        bundle = getIntent().getExtras();
        if (bundle != null) {
//            String image = bundle.getString("img_daidien");
//            String name = bundle.getString("name");

            String img = bundle.getString("img");
            String ten = bundle.getString("nam");
            String tuoi=bundle.getString("age");
            String gt=bundle.getString("gioitinh");
            String cv=bundle.getString("chucvu");
            String no=bundle.getString("noio");
            String email=bundle.getString("email");
            String sn=bundle.getString("ngasinh");

            RoundedImageView roundedImageView=findViewById(R.id.imgProfile);
            TextView nameTextView = findViewById(R.id.tvName);
            TextView name = findViewById(R.id.tv_name);
            TextView date = findViewById(R.id.tv_ngaysinh);
            TextView tui = findViewById(R.id.tv_tuoi);
            TextView gioit = findViewById(R.id.tv_gioitinh);
            TextView chv = findViewById(R.id.tv_chucvu);
            TextView o = findViewById(R.id.tv_noio);
            TextView ema = findViewById(R.id.tv_email);

            recyclerViewBanBe=findViewById(R.id.recycView_ban_be);
            recyclerViewBaiDang=findViewById(R.id.recycView_dang_tin);

            byte[] bytes= Base64.decode(img, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            roundedImageView.setImageBitmap(bitmap);
            nameTextView.setText(ten);
            name.setText(ten);
            tui.setText(tuoi);
            date.setText(sn);
            gioit.setText(gt);
            chv.setText(cv);
            o.setText(no);
            ema.setText(email);


            arrayList=new ArrayList<>();
            getUsers();
            getBaiDang();
        }
    }
    private void getUsers(){
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
                            TrangCaNhanAdapter userAdapter = new TrangCaNhanAdapter(users,ProfileActivity.this);
                            recyclerViewBanBe.setAdapter(userAdapter);
                            recyclerViewBanBe.setVisibility(View.VISIBLE);
                        } else {

                        }
                    } else {

                    }
                });
    }
    private void getBaiDang(){
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
                    if(name.equals(preferencemanager.getString(Constraints.KEY_NAME))){
                        DangTinModel model = new DangTinModel(id, ima_dai_dien, img_dang_tin, video, name, eit, formattedTime, count);
                        arrayList.add(model);
                    }
                    else {

                    }
                }

                TrangCaNhanDangTinAdapter adapter=new TrangCaNhanDangTinAdapter(arrayList,this);
                recyclerViewBaiDang.setAdapter(adapter);
            }
        });
    }
    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd,yyyy-hh:mm a", Locale.getDefault()).format(date);
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