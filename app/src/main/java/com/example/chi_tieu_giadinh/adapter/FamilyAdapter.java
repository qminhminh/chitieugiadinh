package com.example.chi_tieu_giadinh.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chi_tieu_giadinh.databinding.ItemFamilyBinding;
import com.example.chi_tieu_giadinh.model.FamilyModel;
import com.example.chi_tieu_giadinh.taikhoancanhan.ProfileActivity;

import java.util.ArrayList;

public class FamilyAdapter extends RecyclerView.Adapter<FamilyAdapter.FamilyViewHolder> {
    ArrayList<FamilyModel> arrayList=new ArrayList<>();

    public FamilyAdapter(ArrayList<FamilyModel> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public FamilyAdapter.FamilyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFamilyBinding itemFamilyBinding=ItemFamilyBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new FamilyViewHolder(itemFamilyBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FamilyAdapter.FamilyViewHolder holder, int position) {
      holder.setData(arrayList.get(position));
      FamilyModel model=arrayList.get(position);
      // lấy dữ liệu của tưng model để chuyền qua ci tiết của thoong tin
      holder.binding.img.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent intent=new Intent(view.getContext(), ProfileActivity.class);
              intent.putExtra("img",model.getImg());
              intent.putExtra("nam",model.getTen());
              intent.putExtra("age",model.getTuoi());
              intent.putExtra("gioitinh",model.getGioitinh());
              intent.putExtra("chucvu",model.getChucvu());
              intent.putExtra("noio",model.getNoio());
              intent.putExtra("ngasinh",model.getSinhngay());
              intent.putExtra("email",model.getEmail());
              view.getContext().startActivity(intent);
          }
      });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

     class FamilyViewHolder extends RecyclerView.ViewHolder{
        ItemFamilyBinding binding;
         public FamilyViewHolder(ItemFamilyBinding itemFamilyBinding) {
             super(itemFamilyBinding.getRoot());
             binding=itemFamilyBinding;
         }
         void setData(FamilyModel model){
             binding.img.setImageBitmap(getUserImage(model.getImg()));
             binding.tvName.setText(model.getTen());
             binding.tvNgaysinh.setText(model.getSinhngay());
             binding.tvTuoi.setText(model.getTuoi());
             binding.tvGioitinh.setText(model.getGioitinh());
             binding.tvChucvu.setText(model.getChucvu());
             binding.tvNoio.setText(model.getNoio());
             binding.tvEmail.setText(model.getEmail());
         }
     }
    private Bitmap getUserImage(String encodedImage){
        byte[] bytes= Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
