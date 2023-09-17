package com.example.chi_tieu_giadinh.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chi_tieu_giadinh.databinding.ItemContanerUserBinding;
import com.example.chi_tieu_giadinh.interfaccee.UserListeners;
import com.example.chi_tieu_giadinh.model.UserModel;
import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    ArrayList<UserModel> users;
    UserListeners userListeners;

    public UserAdapter(ArrayList<UserModel> users, UserListeners userListeners) {
        this.users = users;
        this.userListeners = userListeners;
    }

    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContanerUserBinding itemContanerUserBinding=ItemContanerUserBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new UserViewHolder(itemContanerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

     class UserViewHolder extends RecyclerView.ViewHolder{
         ItemContanerUserBinding binding;
         public UserViewHolder(ItemContanerUserBinding itemContanerUserBinding) {
             super(itemContanerUserBinding.getRoot());
             binding=itemContanerUserBinding;
         }
         void setUserData(UserModel user){
             binding.textName.setText(user.name);
             binding.textEmail.setText(user.email);
             binding.imgProfile.setImageBitmap(getUserImage(user.image));
             binding.getRoot().setOnClickListener(v->userListeners.onUserClickd(user));
             binding.imgPhone.setOnClickListener(view -> {
                                 userListeners.inittialAudiomeeting(user);
             });
             binding.imgVideo.setOnClickListener(view -> {
                   userListeners.inittialVideomeeting(user);
             });
         }
     }
    private Bitmap getUserImage(String encodedImage){
        byte[] bytes= Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
