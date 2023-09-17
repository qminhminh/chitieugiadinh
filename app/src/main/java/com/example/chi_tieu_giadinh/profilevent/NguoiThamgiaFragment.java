package com.example.chi_tieu_giadinh.profilevent;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.adapter.ThamGiaProfileAdapter;
import com.example.chi_tieu_giadinh.model.CreateEventModel;
import com.example.chi_tieu_giadinh.model.ThamGiaProfile;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;


public class NguoiThamgiaFragment extends Fragment {


    private int pos;
    private CreateEventModel model;
    ArrayList<ThamGiaProfile> arrayList=new ArrayList<>();
    RecyclerView recyclerView;
    public NguoiThamgiaFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nguoi_thamgia, container, false);
        recyclerView=view.findViewById(R.id.recyview_profileevnt);
        Bundle bundle = getArguments();
        if (bundle != null) {
            pos = bundle.getInt("poss");
        }
        getLiseser(pos);
        // Use 'pos' and 'model' as needed
        // ...

        return view;
    }
    private void getLiseser(int position){
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        CollectionReference evenUsersCollectiion=firestore.collection(Constraints.KEY_COLLECTIONS_EVENT_USES);
        CollectionReference evevtUsersRef=evenUsersCollectiion.document(String.valueOf(position)).collection(Constraints.KEY_COLLECTIONS_EVENT_USES);
        evevtUsersRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        QuerySnapshot querySnapshot=task.getResult();
                        for (DocumentSnapshot documentSnapshot:querySnapshot.getDocuments()){
                            String image= documentSnapshot.getString(Constraints.KEY_NAME);
                            String name=documentSnapshot.getString(Constraints.KEY_NAME);
                            ThamGiaProfile model=new ThamGiaProfile(image,name);
                            arrayList.add(model);
                        }
                        ThamGiaProfileAdapter adapter=new ThamGiaProfileAdapter(arrayList);
                        recyclerView.setAdapter(adapter);
                    }
                });
    }
}