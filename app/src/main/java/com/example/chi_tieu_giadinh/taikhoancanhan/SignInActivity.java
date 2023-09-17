package com.example.chi_tieu_giadinh.taikhoancanhan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
import com.example.chi_tieu_giadinh.AdminActivity;
import com.example.chi_tieu_giadinh.MainActivity;
import com.example.chi_tieu_giadinh.databinding.ActivitySignInBinding;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {
    ActivitySignInBinding binding;
    FirebaseAuth firebaseAuth;
    private Preferencemanager preferencemanager;
    FirebaseFirestore fStrore;
    boolean valid=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth=FirebaseAuth.getInstance();
        fStrore=FirebaseFirestore.getInstance();
        preferencemanager=new Preferencemanager(getApplicationContext());
        setListener();
    }
    private void setListener(){
        binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=binding.loginEmail.getText().toString();
                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                               Toast.makeText(SignInActivity.this, "Đã gửi email ", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignInActivity.this, "Chưa gửi email", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        binding.signUpRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this,SignUpActivity.class));

            }
        });
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidInDetail()){
                    signIn();
                }
            }
        });
    }
    //có tác dụng thực hiện quá trình đăng nhập người dùng vào ứng dụng
    private void signIn(){
        //Đầu tiên, lấy giá trị email và password từ các trường nhập liệu
        String email=binding.loginEmail.getText().toString().trim();
        String password=binding.loginPassword.getText().toString().trim();
        //để thực hiện quá trình xác thực đăng nhập người dùng với email và mật khẩu đã cung cấp.
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        checkIfAdmin(authResult.getUser().getUid());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignInActivity.this, "Đăng nhập không thành công", Toast.LENGTH_SHORT).show();
                    }
                });
//Sau đó, truy vấn cơ sở dữ liệu Firestore để kiểm tra thông tin người dùng. Sử dụng phương
// thức whereEqualTo để so khớp email và password của người dùng.
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        database.collection(Constraints.KEY_USES_AUTH)
                .whereEqualTo(Constraints.KEY_EMAIL,binding.loginEmail.getText().toString())
                .whereEqualTo(Constraints.KEY_PASSWORD,binding.loginPassword.getText().toString())
                .get()
                .addOnCompleteListener(task->{
                    if(task.isSuccessful()&&task.getResult()!=null&&task.getResult().getDocumentChanges().size()>0){
                        DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
                        preferencemanager.putBoolean(Constraints.KEY_IS_SIGN_IN,true);
                        preferencemanager.putString(Constraints.KEY_USER_ID, documentSnapshot.getId());
                        preferencemanager.putString(Constraints.KEY_NAME, documentSnapshot.getString(Constraints.KEY_NAME));
                        preferencemanager.putString(Constraints.KEY_IMGE,documentSnapshot.getString(Constraints.KEY_IMGE));
                        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                        //được sử dụng để đặt cờ cho Intent. FLAG_ACTIVITY_NEW_TASK sẽ tạo một tác vụ mới cho màn hình MainActivity,
                        // và FLAG_ACTIVITY_CLEAR_TASK sẽ xóa tất cả các tác vụ và màn hình ở phía sau của MainActivity.
                        //Điều này có nghĩa là sau khi chuyển đến MainActivity,
                        // các màn hình trước đó sẽ bị xóa khỏi ngăn xếp màn hình.
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else {
                        showToast("Unable to sign in");
                    }
                });
    }
     private void checkIfAdmin(String uid){

         DocumentReference df=fStrore.collection(Constraints.KEY_USES_AUTH).document(uid);
         df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
             @Override
             public void onSuccess(DocumentSnapshot documentSnapshot) {
                 Log.d("TAG","success"+documentSnapshot.getData());
                 if(documentSnapshot.getString(Constraints.KEY_IS_ADMIN)!=null){
                     startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                     Toast.makeText(SignInActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                     preferencemanager.putString(Constraints.KEY_EMAIL, binding.loginEmail.getText().toString());
                     finish();
                 }
                 if (documentSnapshot.getString(Constraints.KEY_IS_USERS)!=null){
                     startActivity(new Intent(getApplicationContext(), MainActivity.class));
                     Toast.makeText(SignInActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                     preferencemanager.putString(Constraints.KEY_EMAIL, binding.loginEmail.getText().toString());
                     finish();
                 }
             }
         });
     }
    private Boolean isValidInDetail(){
        if(binding.loginEmail.getText().toString().trim().isEmpty()){
            showToast("Enter email"); return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.loginEmail.getText().toString()).matches()) {
            showToast("Enter valid email");return false;
        } else if (binding.loginPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter your password");return false;
        }else {
            return true;
        }
    }
    private void showToast(String a){
        Toast.makeText(this, a, Toast.LENGTH_SHORT).show();
    }
}