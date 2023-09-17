package com.example.chi_tieu_giadinh.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.adapter.EventAdapter;
import com.example.chi_tieu_giadinh.model.CreateEventModel;
import com.example.chi_tieu_giadinh.model.NoticeModel;
import com.example.chi_tieu_giadinh.profilevent.ProfileEventActivity;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;


public class NoticeFragment extends Fragment implements EventAdapter.Listeners {
    View view;
    ArrayList<NoticeModel> arrayList=new ArrayList<>();
    ArrayList<CreateEventModel> arrayLists=new ArrayList<>();
    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;
    Preferencemanager preferencemanager;
    ImageView imageView;

    public NoticeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view= inflater.inflate(R.layout.fragment_notice, container, false);
         recyclerView=view.findViewById(R.id.recvyview_notice);
         preferencemanager=new Preferencemanager(getContext());

        getSukien();
        return view;
    }

    public void getSukien() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firestored = FirebaseFirestore.getInstance();
        firestored.collection(Constraints.KEY_COLLECTIONS_EVENT)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                            String img = documentSnapshot.getString(Constraints.KEY_IMGE);
                            String name = documentSnapshot.getString(Constraints.KEY_NAME);
                            String noidung = documentSnapshot.getString(Constraints.KEY_EVENT_NOIDUNG);
                            String ghichu = documentSnapshot.getString(Constraints.KEY_EVENT_GHICHU);
                            String date = documentSnapshot.getString(Constraints.KEY_EVENT_NGAY);
                            String time = documentSnapshot.getString(Constraints.KEY_EVENT_TIME);
                            CreateEventModel model = new CreateEventModel(img, name, date, time, noidung, ghichu);
                            arrayLists.add(model);
                        }
                        EventAdapter adapter = new EventAdapter(arrayLists, (EventAdapter.Listeners) this);
                        recyclerView.setAdapter(adapter);
                    }
                });
    }

    @Override
    public void EventListeners(CreateEventModel model, int position) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //CollectionReference eventUsesCollection = firestore.collection(Constraints.KEY_COLLECTIONS_EVENT_USES);
        //DocumentReference eventUseRef = eventUsesCollection.document(String.valueOf(position)).collection(Constraints.KEY_COLLECTIONS_EVENT_USES).document(uid);
        CollectionReference eventUsesCollection = firestore.collection(Constraints.KEY_COLLECTIONS_SUKIEN);
        DocumentReference eventUseRef = eventUsesCollection.document(String.valueOf(position)).collection(Constraints.KEY_COLLECTIONS_EVENT_USES).document(uid);
        HashMap<String, String> ad = new HashMap<>();
        ad.put(Constraints.KEY_NAME, preferencemanager.getString(Constraints.KEY_NAME));
        ad.put(Constraints.KEY_IMGE, preferencemanager.getString(Constraints.KEY_IMGE));

        eventUseRef.set(ad)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Thêm dữ liệu thành công
                      //  Toast.makeText(getApplicationContext(), "Thêm dữ liệu vào Firebase Firestore thành công", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Thêm dữ liệu thất bại
                        //Toast.makeText(getApplicationContext(), "Thêm dữ liệu vào Firebase Firestore thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void DeleteLiset(CreateEventModel model,int position) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference eventUsesCollection = firestore.collection(Constraints.KEY_COLLECTIONS_SUKIEN);
        DocumentReference eventUseRef = eventUsesCollection.document(String.valueOf(position)).collection(Constraints.KEY_COLLECTIONS_EVENT_USES).document(uid);
      //  CollectionReference eventUsesCollection = firestore.collection(Constraints.KEY_COLLECTIONS_EVENT_USES);
       // DocumentReference eventUseRef = eventUsesCollection.document(String.valueOf(position)).collection(Constraints.KEY_COLLECTIONS_EVENT_USES).document(uid);

        eventUseRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                       // Toast.makeText(CreteEventActivity.this, "huy thanh cong", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void ClickItem(CreateEventModel model, int position) {
        Intent intent=new Intent(getContext(), ProfileEventActivity.class);
        intent.putExtra("poss", position);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference eventUsesCollection = firestore.collection(Constraints.KEY_COLLECTIONS_SUKIEN);
        DocumentReference eventUseRef = eventUsesCollection.document(String.valueOf(position)).collection(Constraints.KEY_COLLECTIONS_SUKIEN).document(uid);
        HashMap<Object,String> a=new HashMap<>();
        a.put(Constraints.KEY_EVENT_NGAY, model.getDate());
        a.put(Constraints.KEY_EVENT_TIME,model.getTime());
        a.put(Constraints.KEY_EVENT_NOIDUNG, model.getNoidung());
        a.put(Constraints.KEY_EVENT_GHICHU, model.getGhichu());
        eventUseRef.set(a)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });
        getContext().startActivity(intent);
    }

    @Override
    public void CloseListeners(CreateEventModel model, int position) {
        if(preferencemanager.getString(Constraints.KEY_NAME).equals( model.getName())){
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            CollectionReference eventUsesCollection = firestore.collection(Constraints.KEY_COLLECTIONS_EVENT);
            DocumentReference eventUseRef = eventUsesCollection.document(uid);
            eventUseRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                    // xoa proflie chi tiet trỏng su kien
                    CollectionReference eventUsesCollectionprofilr = firestore.collection(Constraints.KEY_COLLECTIONS_SUKIEN);
                    DocumentReference eventUseRefprofilr =  eventUsesCollectionprofilr.document(String.valueOf(position)).collection(Constraints.KEY_COLLECTIONS_SUKIEN).document(uid);
                    eventUseRefprofilr.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //xoa nguoi dung tỏng su kien
                            CollectionReference eventUsesCollection = firestore.collection(Constraints.KEY_COLLECTIONS_SUKIEN);
                            DocumentReference eventUseRef = eventUsesCollection.document(String.valueOf(position)).collection(Constraints.KEY_COLLECTIONS_EVENT_USES).document(uid);
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                            eventUseRef.delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            // Toast.makeText(CreteEventActivity.this, "huy thanh cong", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getContext(), "Xoa ENET USES THANH CONG", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });


                }
            });
        }
        else {
            Toast.makeText(getContext(), "Không xóa được", Toast.LENGTH_SHORT).show();
        }
    }
}