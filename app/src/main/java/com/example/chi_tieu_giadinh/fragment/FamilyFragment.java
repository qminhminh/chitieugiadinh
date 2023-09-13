package com.example.chi_tieu_giadinh.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.adapter.FamilyAdapter;
import com.example.chi_tieu_giadinh.model.FamilyModel;
import com.example.chi_tieu_giadinh.taikhoancanhan.MapActivity;
import com.example.chi_tieu_giadinh.taikhoancanhan.MomentFamilyActivity;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class FamilyFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    ArrayList<FamilyModel> arrayList = new ArrayList<>();
    FirebaseFirestore db;
    FamilyAdapter adapter;
    LinearLayout layout;
    Toolbar toolbar;
    TextView textView;
    ImageView loca;

    public FamilyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_family, container, false);
        recyclerView = view.findViewById(R.id.recyview_giadinh);
        layout=view.findViewById(R.id.linear_search);
        textView=view.findViewById(R.id.tv_search);
        loca=view.findViewById(R.id.iv_location);
        loca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MapActivity.class));
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             startActivity(new Intent(getContext(), MomentFamilyActivity.class));
            }
        });
       // toolbar = view.findViewById(R.id.toolbar_search);
        arrayList = new ArrayList<>();
        adapter = new FamilyAdapter(arrayList);
        getListeners();
        return view;
    }


    private void getListeners() {
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
    }
}
