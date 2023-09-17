package com.example.chi_tieu_giadinh.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chi_tieu_giadinh.databinding.ItemContanerUserTrangcanhanBinding;
import com.example.chi_tieu_giadinh.interfaccee.UserListeners;
import com.example.chi_tieu_giadinh.model.UserModel;
import java.util.ArrayList;

public class TrangCaNhanAdapter extends RecyclerView.Adapter<TrangCaNhanAdapter.TrangCaNhanViewHolder>{
    ArrayList<UserModel> users;
    UserListeners userListeners;

    public TrangCaNhanAdapter(ArrayList<UserModel> users, UserListeners userListeners) {
        this.users = users;
        this.userListeners = userListeners;
    }

    @NonNull
    @Override
    public TrangCaNhanAdapter.TrangCaNhanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContanerUserTrangcanhanBinding itemContanerUserBinding=ItemContanerUserTrangcanhanBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new TrangCaNhanAdapter.TrangCaNhanViewHolder(itemContanerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TrangCaNhanAdapter.TrangCaNhanViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class TrangCaNhanViewHolder extends RecyclerView.ViewHolder{
        ItemContanerUserTrangcanhanBinding binding;
        public TrangCaNhanViewHolder(ItemContanerUserTrangcanhanBinding itemContanerUserBinding) {
            super(itemContanerUserBinding.getRoot());
            binding=itemContanerUserBinding;
        }
        void setUserData(UserModel user){
            binding.textName.setText(user.name);
            binding.imgProfile.setImageBitmap(getUserImage(user.image));
            binding.getRoot().setOnClickListener(v->userListeners.onUserClickd(user));
        }
    }
    private Bitmap getUserImage(String encodedImage){
        byte[] bytes= Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
