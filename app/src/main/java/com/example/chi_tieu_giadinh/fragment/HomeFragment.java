package com.example.chi_tieu_giadinh.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.chi_tieu_giadinh.AdminActivity;
import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.adapter.DangTinAdapter;
import com.example.chi_tieu_giadinh.adapter.EventAdapter;
import com.example.chi_tieu_giadinh.adapter.StoryAdapter;
import com.example.chi_tieu_giadinh.interfaccee.DangTinInterface;
import com.example.chi_tieu_giadinh.khoangkhac.KhoangKhacImageActivity;
import com.example.chi_tieu_giadinh.model.CreateEventModel;
import com.example.chi_tieu_giadinh.model.DangTinModel;
import com.example.chi_tieu_giadinh.model.ModelStrory;
import com.example.chi_tieu_giadinh.taikhoancanhan.AddDangTinActivity;
import com.example.chi_tieu_giadinh.taikhoancanhan.CommentActivity;
import com.example.chi_tieu_giadinh.taikhoancanhan.StoryActivity;
import com.example.chi_tieu_giadinh.taikhoancanhan.TrangCaNhanActivity;
import com.example.chi_tieu_giadinh.taikhoancanhan.TrangChinhChatActivity;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class HomeFragment extends Fragment implements DangTinAdapter.Listeners {

ImageView image_add,img_story;
RecyclerView rycy;
RecyclerView recyclerViewStory;
TextView count_no;
View view;
ImageView image_chat,notice,image_img;
CardView cardView;
ArrayList<DangTinModel> arrayList;
ArrayList<ModelStrory> stroryArrayList;
   // ArrayList<CreateEventModel> arrayLists;

    public HomeFragment() {
        // Required empty public constructor
    }



Preferencemanager preferencemanager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_home, container, false);
        image_add=view.findViewById(R.id.id_add_tin);
        rycy=view.findViewById(R.id.recyview);
        image_chat=view.findViewById(R.id.iv_chat);
        notice=view.findViewById(R.id.iv_notice);
        img_story=view.findViewById(R.id.imgandvideo);
        count_no=view.findViewById(R.id.count_notice);
        preferencemanager=new Preferencemanager(getContext());
        cardView=view.findViewById(R.id.addstory);
        recyclerViewStory=view.findViewById(R.id.recyviewStory);

        //=====================================
        //=================================
        setListeners();
        arrayList = new ArrayList<>();
        stroryArrayList=new ArrayList<>();
        //arrayLists=new ArrayList<>();
        getTin();
       // getSukien();
        countDay();
        loadUserDetails();
        return view;
    }
    private void countDay(){
        FirebaseFirestore data=FirebaseFirestore.getInstance();
        data.collection(Constraints.KEY_COUNT_NOTICE)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot documentSnapshot: task.getResult()){
                            if (documentSnapshot.contains("countnotice")) {
                                Long count = documentSnapshot.getLong("countnotice");
                                if (count != null) {
                                    count_no.setText(String.valueOf(count));
                                } else {
                                    // Xử lý khi giá trị là null
                                    count_no.setVisibility(View.GONE);
                                }
                            } else {
                                // Xử lý khi trường không tồn tại trong tài liệu
                            }

                        }

                    }
                });
    }
    private void loadUserDetails(){
        if (preferencemanager == null) {
            preferencemanager = new Preferencemanager(getContext());
        }
        String imageString = preferencemanager.getString(Constraints.KEY_IMGE);
        if (imageString != null) {
            byte[] bytes= Base64.decode(imageString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            img_story.setImageBitmap(bitmap);
        }

    }
    private void getTin(){
        // add tin
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        firestore.collection(Constraints.KEY_COLECTION_ADD_TIN)
                .get()
                .addOnCompleteListener(task -> {
                  if (task.isSuccessful()){
                      for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                          String ima_dai_dien=documentSnapshot.getString(Constraints.KEY_COLECTION_ADD_IMAGE_DAI_DIEN);
                          String img_dang_tin=documentSnapshot.getString(Constraints.KEY_COLECTION_ADD_IMAGE);
                          String eit=documentSnapshot.getString(Constraints.KEY_COLECTION_ADD_EDITTEXT);
                          String video=documentSnapshot.getString(Constraints.KEY_COLECTION_ADD_VIDEO);
                          String name=documentSnapshot.getString(Constraints.KEY_COLECTION_ADD_NAME);
                          Date time = documentSnapshot.getDate(Constraints.KEY_COLECTION_TIME);
                          String formattedTime = "";
                          if (time != null) {
                              formattedTime = getReadableDateTime(time);
                          }

                          long count = 0; // Giá trị mặc định
                          if (documentSnapshot.contains(Constraints.KEY_COLECTION_ADD_COUNT)) {
                              count = documentSnapshot.getLong(Constraints.KEY_COLECTION_ADD_COUNT);
                          }

                          String id=documentSnapshot.getId();
                          DangTinModel model=new DangTinModel(id,ima_dai_dien,img_dang_tin,video,name,eit,formattedTime,count);
                          arrayList.add(model);
                      }
                      DangTinAdapter adapter = new DangTinAdapter(arrayList, this,getContext());
                      rycy.setAdapter(adapter);
                  }
                });
        // add strory
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String authId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection(Constraints.KEY_COLLECTIONS_VIDEO_STRORY)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        long currentTimeMillis = System.currentTimeMillis();
                        long twentyFourHoursInMillis = 24 * 60 * 60 * 1000;
                        for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                            long timestamp = documentSnapshot.getTimestamp("timestamp").toDate().getTime();
                            if (currentTimeMillis - timestamp <= twentyFourHoursInMillis) {
                                String name = documentSnapshot.getString(Constraints.KEY_NAME);
                                String des = documentSnapshot.getString(Constraints.KEY_STORY_DESCRPIT);
                                String img = documentSnapshot.getString(Constraints.KEY_IMGE);
                                String video = documentSnapshot.getString(Constraints.KEY_STORY_VIDEO);
                                ModelStrory modelStrory = new ModelStrory(name, video, img, des);
                                stroryArrayList.add(modelStrory);
                            }

                        }
                        StoryAdapter adapter=new StoryAdapter(stroryArrayList,getContext());
                        recyclerViewStory.setAdapter(adapter);
                    }
                });



    }

    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd,yyyy-hh:mm a", Locale.getDefault()).format(date);
    }
    private void setListeners(){
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), StoryActivity.class));
            }
        });
        int tuoi= Integer.parseInt(preferencemanager.getString(Constraints.KEY_DONGHO));
        if (tuoi>=18){
            image_add.setVisibility(View.VISIBLE);
            image_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getContext(), AddDangTinActivity.class));
                }
            });
        }
        else {
            image_add.setVisibility(View.GONE);
        }

        image_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), TrangChinhChatActivity.class));
            }
        });
        notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new NoticeFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

    }


    @Override
    public void CountListeners(int pos) {
        Intent messengerIntent = new Intent(Intent.ACTION_SEND);
        messengerIntent.setType("text/plain");
        messengerIntent.putExtra(Intent.EXTRA_TEXT, "message");
        messengerIntent.setPackage("com.facebook.orca");
        startActivity(messengerIntent);
    }

    @Override
    public void ComentListensers(int pos) {
        Intent intent=new Intent(getContext(), CommentActivity.class);
        intent.putExtra("pos", pos);
        getContext().startActivity(intent);
    }

    @Override
    public void TrangcanhanListener(int pos) {
        Intent intent=new Intent(getContext(), TrangCaNhanActivity.class);
        getContext().startActivity(intent);
    }

    @Override
    public void CloseListeners(DangTinModel model, int positon) {
        if(preferencemanager.getString(Constraints.KEY_NAME).equals(model.getName())){

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            CollectionReference eventUsesCollection = firestore.collection(Constraints.KEY_COLECTION_ADD_TIN);
            DocumentReference eventUseRef = eventUsesCollection.document(String.valueOf(model.getId()));
            eventUseRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    CollectionReference eventUsesCollection = firestore.collection(Constraints.KEY_COLECTION_ADD_COMMENT);
                    DocumentReference eventUseRef = eventUsesCollection.document(String.valueOf(positon)).collection(Constraints.KEY_COLECTION_ADD_COMMENT).document(uid);
                    eventUseRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                           public void onSuccess(Void unused) {
                           Toast.makeText(getContext(), "Xóa bai dang thành công", Toast.LENGTH_SHORT).show();
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