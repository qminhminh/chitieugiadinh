package com.example.chi_tieu_giadinh.profilevent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.chi_tieu_giadinh.adapter.ThamGiaProfileAdapter;
import com.example.chi_tieu_giadinh.databinding.ActivityProfileEventBinding;
import com.example.chi_tieu_giadinh.model.ThamGiaProfile;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

public class ProfileEventActivity extends AppCompatActivity {
    ActivityProfileEventBinding binding;
    int count=0;

    ArrayList<ThamGiaProfile> arrayList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityProfileEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        int pos = getIntent().getIntExtra("poss",0); // Nhận vị trí từ Intent
        getLiseser(pos);
        getModel(pos);


    }
    private void getModel(int posi){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference eventUsesCollection = firestore.collection(Constraints.KEY_COLLECTIONS_SUKIEN);
        CollectionReference eventUseRef = eventUsesCollection.document(String.valueOf(posi)).collection(Constraints.KEY_COLLECTIONS_SUKIEN);
        eventUseRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                   String date=documentSnapshot.getString(Constraints.KEY_EVENT_NGAY);
                   String time =documentSnapshot.getString(Constraints.KEY_EVENT_TIME);
                   String ghichu=documentSnapshot.getString(Constraints.KEY_EVENT_GHICHU);
                   String Noidung=documentSnapshot.getString(Constraints.KEY_EVENT_NOIDUNG);
                   binding.tvNgaydi.setText(date);
                   binding.tvGiodi.setText(time);
                   binding.tvNd.setText(ghichu);
                   binding.tvGhichu.setText(Noidung);
                }
            }
        });

    }
    private void getLiseser(int position){
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();

        CollectionReference eventUsesCollection = firestore.collection(Constraints.KEY_COLLECTIONS_SUKIEN);
        CollectionReference eventUseRef = eventUsesCollection.document(String.valueOf(position)).collection(Constraints.KEY_COLLECTIONS_EVENT_USES);
        eventUseRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        QuerySnapshot querySnapshot=task.getResult();
                        for (DocumentSnapshot documentSnapshot:querySnapshot.getDocuments()){
                            String image= documentSnapshot.getString(Constraints.KEY_IMGE);
                            String name=documentSnapshot.getString(Constraints.KEY_NAME);
                            ThamGiaProfile model=new ThamGiaProfile(image,name);
                            arrayList.add(model);
                        }
                        ThamGiaProfileAdapter adapter=new ThamGiaProfileAdapter(arrayList);
                        binding.recyviewProfileevnt.setAdapter(adapter);
                        binding.coutUserEvent.setText(String.valueOf(arrayList.size()));
                    }
                });


    }
}