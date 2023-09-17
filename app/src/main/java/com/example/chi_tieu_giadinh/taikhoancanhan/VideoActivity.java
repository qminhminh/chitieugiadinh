package com.example.chi_tieu_giadinh.taikhoancanhan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;
import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.adapter.VideoAdapter;
import com.example.chi_tieu_giadinh.model.ModelStrory;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        final ViewPager2 videoviewPager=findViewById(R.id.videoViewPager);
        List<ModelStrory> videoItems=new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String authId= FirebaseAuth.getInstance().getCurrentUser().getUid();
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
                                videoItems.add(modelStrory);
                            }

                        }
                        videoviewPager.setAdapter(new VideoAdapter(videoItems,this));
                    }
                });
    }
}