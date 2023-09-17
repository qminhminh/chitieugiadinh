package com.example.chi_tieu_giadinh.adapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chi_tieu_giadinh.databinding.ItemContanerConversionBinding;
import com.example.chi_tieu_giadinh.interfaccee.ConversionListener;
import com.example.chi_tieu_giadinh.model.ChatMessageModel;
import com.example.chi_tieu_giadinh.model.UserModel;
import java.util.ArrayList;

public class RecentConversionAdapter extends RecyclerView.Adapter<RecentConversionAdapter.ConversionViewHolder> {
   ArrayList<ChatMessageModel> chatMessage;
   ConversionListener conversionListener;

    public RecentConversionAdapter(ArrayList<ChatMessageModel> chatMessage, ConversionListener conversionListener) {
        this.chatMessage = chatMessage;
        this.conversionListener = conversionListener;
    }

    @NonNull
    @Override
    public RecentConversionAdapter.ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
                ItemContanerConversionBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RecentConversionAdapter.ConversionViewHolder holder, int position) {
        holder.setData(chatMessage.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessage.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder{
       ItemContanerConversionBinding binding;
        public ConversionViewHolder(ItemContanerConversionBinding itemContanerConversionBinding) {
            super(itemContanerConversionBinding.getRoot());
            binding=itemContanerConversionBinding;
        }
        void setData(ChatMessageModel chatMessage){
            binding.imgProfile.setImageBitmap(getConversionImage(chatMessage.conversionImge));
            binding.textName.setText(chatMessage.conversionname);
            binding.textRecentMessage.setText(chatMessage.message);
            binding.getRoot().setOnClickListener(v->{
                UserModel user=new UserModel();
                user.id=chatMessage.conversionId;
                user.image=chatMessage.conversionImge;
                user.name=chatMessage.conversionname;
                conversionListener.onConversionCliked(user);
            });
            binding.imgPhone.setOnClickListener(view -> {
                UserModel user=new UserModel();
                conversionListener.inittialAudiomeeting(user);
            });
            binding.imgVideo.setOnClickListener(view -> {
                UserModel user=new UserModel();
                conversionListener.inittialVideomeeting(user);
            });
        }
    }
    private Bitmap getConversionImage(String encodedImage) {
        if (encodedImage != null) {
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return null; // Return null bitmap if encodedImage is null
    }

}
