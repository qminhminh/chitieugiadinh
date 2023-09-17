package com.example.chi_tieu_giadinh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.chi_tieu_giadinh.adapter.CheckTinDangAdapter;
import com.example.chi_tieu_giadinh.model.CheckTinModel;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AdminActivity extends AppCompatActivity implements CheckTinDangAdapter.Listeners {
ArrayList<CheckTinModel> arrayList;
RecyclerView recyclerView;
Preferencemanager preferencemanager;
TextView checkuser;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        arrayList=new ArrayList<>();
        recyclerView=findViewById(R.id.recyc_admin_check_tin);
        checkuser=findViewById(R.id.chceck_adim_users);
        preferencemanager=new Preferencemanager(getApplicationContext());
        getFirebase();
        setLiseners();
    }
    private void setLiseners(){
        checkuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this,AdminTaiKhoanActivity.class));
            }
        });
    }

    private void getFirebase(){
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        firestore.collection(Constraints.KEY_COLLECTIONS_CHECK_TIN)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                            String ima_dai_dien=documentSnapshot.getString(Constraints.KEY_CHECK_IMG);
                            String img_dang_tin=documentSnapshot.getString(Constraints.KEY_CHECK_ANH_DANG);
                            String eit=documentSnapshot.getString(Constraints.KEY_CHECK_DES);
                            String video=documentSnapshot.getString(Constraints.KEY_CHECK_VIDEO);
                            String name=documentSnapshot.getString(Constraints.KEY_CHECK_NAME);
                            Date time = documentSnapshot.getDate(Constraints.KEY_COLECTION_TIME);
                            String email= documentSnapshot.getString(Constraints.KEY_EMAIL);
                            String formattedTime = "";
                            if (time != null) {
                                formattedTime = getReadableDateTime(time);
                            }

                            long count = 0; // Giá trị mặc định
                            if (documentSnapshot.contains(Constraints.KEY_COLECTION_ADD_COUNT)) {
                                count = documentSnapshot.getLong(Constraints.KEY_COLECTION_ADD_COUNT);
                            }

                            String id=documentSnapshot.getId();
                            CheckTinModel model=new CheckTinModel(id,ima_dai_dien,img_dang_tin,video,name,eit,formattedTime,email,count);
                            arrayList.add(model);
                        }

                        CheckTinDangAdapter adapter = new  CheckTinDangAdapter(arrayList, this,this);
                        recyclerView.setAdapter(adapter);
                    }
                });
    }
    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd,yyyy-hh:mm a", Locale.getDefault()).format(date);
    }
    @Override
    public void DuyetListeners(CheckTinModel model) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference eventUsesCollection = firestore.collection(Constraints.KEY_COLECTION_ADD_TIN);
        DocumentReference eventUseRef = eventUsesCollection.document(model.getId());
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put(Constraints.KEY_ADD_EMAIL, model.getEmail());
        hashMap.put(Constraints.KEY_COLECTION_ADD_IMAGE, model.getImgDangTin());
        hashMap.put(Constraints.KEY_COLECTION_ADD_NAME, model.getName());
        hashMap.put(Constraints.KEY_COLECTION_ADD_EDITTEXT, model.getEdit());
        hashMap.put(Constraints.KEY_COLECTION_ADD_IMAGE_DAI_DIEN, model.getImgDaidien());
        hashMap.put(Constraints.KEY_COLECTION_ADD_VIDEO, model.getVideo());
        eventUseRef.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(AdminActivity.this, "Được duyệt", Toast.LENGTH_SHORT).show();
                CollectionReference eventUsesCollectionadd = firestore.collection(Constraints.KEY_COLLECTIONS_ADDTIN_TRANGCN);
                DocumentReference eventUseRef = eventUsesCollectionadd.document(uid);
                eventUseRef.set(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });
            }
        });


    }
}