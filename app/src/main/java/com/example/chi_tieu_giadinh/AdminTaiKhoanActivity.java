package com.example.chi_tieu_giadinh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.chi_tieu_giadinh.adapter.AdminFamilyAdapter;
import com.example.chi_tieu_giadinh.model.AdminFamilyModel;
import com.example.chi_tieu_giadinh.taikhoancanhan.SignInActivity;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;


public class AdminTaiKhoanActivity extends AppCompatActivity implements AdminFamilyAdapter.Listeners {

    ImageView imageView;
    RecyclerView recyclerView;
    ArrayList<AdminFamilyModel> arrayList;
    FirebaseFirestore db;
    AdminFamilyAdapter adapter;
    FirebaseAuth firebaseAuth;
    private Preferencemanager preferencemanager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_tai_khoan);
        imageView = findViewById(R.id.img_logout_admin);
        recyclerView = findViewById(R.id.recycy_admin_user);
        preferencemanager = new Preferencemanager(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        arrayList = new ArrayList<>();
        setListeners();
        getFirebase();
    }

    private void setListeners() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }

    private void getFirebase() {
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
                                AdminFamilyModel model = new AdminFamilyModel(id, img, name, ngaysinh, age, gioitinh, chucvu, noio, email);
                                arrayList.add(model);
                            }
                            adapter = new AdminFamilyAdapter(arrayList, AdminTaiKhoanActivity.this);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void DeleteFamilyListeners(AdminFamilyModel model) {
        showDeleteConfirmationDialog(model);
    }

    private void showDeleteConfirmationDialog(AdminFamilyModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bạn muốn xóa tài khoản: "+model.getEmail());
        builder.setMessage("Bạn có chắc muốn xóa không?");
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteUserAccount(model);
            }
        });
        builder.setNegativeButton("Hủy", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteUserAccount(AdminFamilyModel model) {
        String userId = model.getId(); // Lấy ID của người dùng cần xóa
        FirebaseFirestore.getInstance()
                .collection(Constraints.KEY_USES_AUTH)
                .document(userId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AdminTaiKhoanActivity.this, "Tài khoản xóa thành công", Toast.LENGTH_SHORT).show();
                        arrayList.remove(model); // Xóa tài khoản khỏi danh sách arrayList
                        adapter.notifyDataSetChanged(); // Cập nhật giao diện sau khi xóa
                        String emailToDelete = model.getEmail(); // Get the email of the user to delete
                        if (emailToDelete != null && !emailToDelete.isEmpty()) {
                            deleteAccountByEmail(emailToDelete);
                        } else {
                            Toast.makeText(AdminTaiKhoanActivity.this, "User email is not available", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminTaiKhoanActivity.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                    }
                });

    }
    // Add this method to your AdminTaiKhoanActivity class
    private void deleteAccountByEmail(String emailToDelete) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Find the user by email
        auth.fetchSignInMethodsForEmail(emailToDelete)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            SignInMethodQueryResult result = task.getResult();
                            if (result != null && result.getSignInMethods() != null && !result.getSignInMethods().isEmpty()) {

                                String userId = result.getSignInMethods().get(0);
                            } else {

                               // Toast.makeText(AdminTaiKhoanActivity.this, "User account with email " + emailToDelete + " not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                           // Toast.makeText(AdminTaiKhoanActivity.this, "Failed to query Firebase Authentication", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
