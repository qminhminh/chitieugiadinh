package com.example.chi_tieu_giadinh.adapter;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chi_tieu_giadinh.databinding.ItemNoticeBinding;
import com.example.chi_tieu_giadinh.model.NoticeModel;
import java.util.ArrayList;


public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {
    ArrayList<NoticeModel> arrayList;

    public NoticeAdapter(ArrayList<NoticeModel> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public NoticeAdapter.NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNoticeBinding itemNoticeBinding=ItemNoticeBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new NoticeViewHolder(itemNoticeBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeAdapter.NoticeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        NoticeModel model=arrayList.get(position);
       holder.setData(arrayList.get(position));

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

     class NoticeViewHolder extends RecyclerView.ViewHolder{
        ItemNoticeBinding binding;
         public NoticeViewHolder(ItemNoticeBinding itemNoticeBinding) {
             super(itemNoticeBinding.getRoot());
             binding=itemNoticeBinding;
         }
         void setData(NoticeModel model){
             binding.textName.setText(model.name);
             binding.textNotice.setText(model.notice);
             binding.imgProfile.setImageBitmap(getUserImage(model.img));
         }
     }
    private Bitmap getUserImage(String encodedImage){
        if (encodedImage == null) {
            return null;
        }
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
