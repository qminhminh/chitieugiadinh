package com.example.chi_tieu_giadinh.khoangkhac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.fragment.HomeFragment;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class KhoangKhacImageActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FloatingActionButton fab;
    ArrayList<DataClass> dataList;
    ImageView imageView;
    MyAdapter adapter;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Images");
    final private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khoang_khac_image);
        fab = findViewById(R.id.fab);
        imageView = findViewById(R.id.imgBack);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        dataList = new ArrayList<>();
        adapter = new MyAdapter(dataList,this);
        recyclerView.setAdapter(adapter);
        firebaseFirestore.collection(Constraints.KEY_COLLECTIONS_KHOANGKHAC)
                .get()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                for (QueryDocumentSnapshot documentSnapshot:task.getResult()){
                                  String img= documentSnapshot.getString(Constraints.KEY_KHOANGKHAC_IMG);
                                  String cap= documentSnapshot.getString(Constraints.KEY_KHOANGKHAC_CAPTION);
                                  DataClass dataClass=new DataClass(img,cap);
                                  dataList.add(dataClass);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        });

        setListensers();
    }
    private void setListensers(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(KhoangKhacImageActivity.this, UploadActivity.class);
                startActivity(intent);
                finish();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
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
}