package com.example.chi_tieu_giadinh.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.taikhoancanhan.SignInActivity;
import com.example.chi_tieu_giadinh.taikhoancanhan.TrangCaNhanActivity;
import com.example.chi_tieu_giadinh.taikhoancanhan.UpdateUsersActivity;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.HashMap;


public class MenuFragment extends Fragment {

    private Preferencemanager preferencemanager;
    View view;
    RoundedImageView img;
    TextView textame,textlogout,textupdate;
    ConstraintLayout constraintLayout;
    public MenuFragment() {
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
        view= inflater.inflate(R.layout.fragment_menu, container, false);
        textame=view.findViewById(R.id.textName);
        textlogout=view.findViewById(R.id.imgSignOut);
        img=view.findViewById(R.id.imgProfile);
        constraintLayout=view.findViewById(R.id.liner);
        textupdate=view.findViewById(R.id.imgUpdate);
        loadUserDetails();
        setListeners();
        return view;
    }
    private void loadUserDetails(){
        if (preferencemanager == null) {
            preferencemanager = new Preferencemanager(getContext());
        }
        textame.setText(preferencemanager.getString(Constraints.KEY_NAME));
        String imageString = preferencemanager.getString(Constraints.KEY_IMGE);
        if (imageString != null) {
            byte[] bytes= Base64.decode(imageString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            img.setImageBitmap(bitmap);
        }

    }
    private void setListeners(){
        textlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), TrangCaNhanActivity.class);
                getContext().startActivity(intent);
            }
        });
        textupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), UpdateUsersActivity.class);
                getContext().startActivity(intent);
            }
        });

    }
    private void signOut(){

        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getContext(), SignInActivity.class));


        // delete token

        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        DocumentReference documentReference=firestore.collection(Constraints.KEY_USES_AUTH).document(
          preferencemanager.getString(Constraints.KEY_USER_ID)
        );
        HashMap<String,Object> updates=new HashMap<>();
        updates.put(Constraints.KEY_FCM_TOKEN,FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    private void showToast(String a){
        Toast.makeText(getContext(), a, Toast.LENGTH_SHORT).show();
    }
}