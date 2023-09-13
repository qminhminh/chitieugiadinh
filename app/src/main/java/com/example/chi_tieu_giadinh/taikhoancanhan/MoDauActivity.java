package com.example.chi_tieu_giadinh.taikhoancanhan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.chi_tieu_giadinh.AdminActivity;
import com.example.chi_tieu_giadinh.MainActivity;
import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.databinding.ActivityMoDauBinding;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MoDauActivity extends AppCompatActivity {
    ActivityMoDauBinding binding;
    private Preferencemanager preferencemanager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMoDauBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferencemanager=new Preferencemanager(getApplicationContext());
//        if(preferencemanager.getBoolean(Constraints.KEY_IS_SIGN_IN)){
//            Intent intent=new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
//            finish();
//        }

        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SignInActivity.class));
            }
        });
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            DocumentReference df= FirebaseFirestore.getInstance().collection(Constraints.KEY_USES_AUTH)
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.getString(Constraints.KEY_IS_ADMIN)!=null){
                        startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                        finish();
                    }
                    if(documentSnapshot.getString(Constraints.KEY_IS_USERS)!=null){
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(getApplicationContext(),SignInActivity.class));
                            finish();
                        }
                    });
        }
    }
}