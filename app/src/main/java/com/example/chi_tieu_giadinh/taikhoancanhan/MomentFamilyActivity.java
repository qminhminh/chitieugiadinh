package com.example.chi_tieu_giadinh.taikhoancanhan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.adapter.FamilyAdapter;
import com.example.chi_tieu_giadinh.model.FamilyModel;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MomentFamilyActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    FamilyAdapter adapter;
    ArrayList<FamilyModel> arrayList;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_family);
        recyclerView=findViewById(R.id.recleset2_search);
        Toolbar toolbar=findViewById(R.id.toolbar_search);

        arrayList=new ArrayList<>();
        adapter=new FamilyAdapter(arrayList);
        db=FirebaseFirestore.getInstance();
        db = FirebaseFirestore.getInstance();
        db.collection(Constraints.KEY_USES_AUTH)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                String img = documentSnapshot.getString(Constraints.KEY_IMGE);
                                String name = documentSnapshot.getString(Constraints.KEY_NAME);
                                String age = documentSnapshot.getString(Constraints.KEY_DONGHO);
                                String chucvu = documentSnapshot.getString(Constraints.KEY_CHUCVU);
                                String ngaysinh = documentSnapshot.getString(Constraints.KEY_NGAYTHANGNAM);
                                String noio = documentSnapshot.getString(Constraints.KEY_NOIO);
                                String gioitinh = documentSnapshot.getString(Constraints.KEY_GIOITINH);
                                String email = documentSnapshot.getString(Constraints.KEY_EMAIL);
                                String id = documentSnapshot.getId();
                                FamilyModel model = new FamilyModel(id, img, name, ngaysinh, age, gioitinh, chucvu, noio, email);
                                arrayList.add(model);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
        recyclerView.setAdapter(adapter);
        adapter=new FamilyAdapter(arrayList);
        recyclerView.setAdapter(adapter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nu,menu);
        MenuItem item=menu.findItem(R.id.sreach);
        SearchView searchView= (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String searchValue = newText;
                if (!searchValue.isEmpty()) {
                    // Chuyển đổi chữ cái đầu tiên sang chữ hoa
                    searchValue = searchValue.substring(0, 1).toUpperCase() + searchValue.substring(1);
                }

                mysearch(searchValue);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void mysearch(String newText) {
        arrayList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        db.collection(Constraints.KEY_COLECTION_USERS)
                .whereGreaterThanOrEqualTo(Constraints.KEY_NAME, newText)
                .orderBy(Constraints.KEY_NAME)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                String img = documentSnapshot.getString(Constraints.KEY_IMGE);
                                String name = documentSnapshot.getString(Constraints.KEY_NAME);
                                String age = documentSnapshot.getString(Constraints.KEY_DONGHO);
                                String chucvu = documentSnapshot.getString(Constraints.KEY_CHUCVU);
                                String ngaysinh = documentSnapshot.getString(Constraints.KEY_NGAYTHANGNAM);
                                String noio = documentSnapshot.getString(Constraints.KEY_NOIO);
                                String gioitinh = documentSnapshot.getString(Constraints.KEY_GIOITINH);
                                String email = documentSnapshot.getString(Constraints.KEY_EMAIL);
                                String id = documentSnapshot.getId();
                                FamilyModel model = new FamilyModel(id, img, name, ngaysinh, age, gioitinh, chucvu, noio, email);
                                arrayList.add(model);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Error getting documents: ", e);
                    }
                });
       adapter = new FamilyAdapter(arrayList);
        recyclerView.setAdapter(adapter);
    }
}