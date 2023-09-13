package com.example.chi_tieu_giadinh.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.model.DangTinModel;
import com.example.chi_tieu_giadinh.model.ModelStrory;
import com.example.chi_tieu_giadinh.taikhoancanhan.VideoActivity;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.Serializable;
import java.util.ArrayList;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StroryViewHolder> {
    ArrayList<ModelStrory> arrayList;
    Context context;

    public StoryAdapter(ArrayList<ModelStrory> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public StoryAdapter.StroryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story,parent,false);
        return new StoryAdapter.StroryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryAdapter.StroryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ModelStrory modelStrory=arrayList.get(position);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, VideoActivity.class);
                intent.putExtra("modelStory", modelStrory);
                intent.putExtra("name",modelStrory.getName());
                intent.putExtra("des",modelStrory.getText());
                context.startActivity(intent);
            }
        });
        if(modelStrory.getName()!=null){
            holder.name.setText(modelStrory.getName());
        }else {
            holder.name.setText(null);
        }
        if(modelStrory.getText()!=null){
            holder.descripttion.setText(modelStrory.getText());
        }else {
            holder.descripttion.setText(null);
        }
        if(modelStrory.getName()!=null){
            holder.name.setText(modelStrory.getName());
        }
        else {
            holder.name.setText(null);
        }
        if(modelStrory.getImgdaidien()!=null){
            holder.anh_dai_dien.setImageBitmap(getUserImage(modelStrory.getImgdaidien()));
        }
        else {
            holder.anh_dai_dien.setImageBitmap(null);
        }
        if(modelStrory.getVideo()!=null){
            holder.videoView.setVideoPath(modelStrory.getVideo());
            holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    float videoRatio=mediaPlayer.getVideoWidth()/(float) mediaPlayer.getVideoHeight();
                    float screenRatio=holder.videoView.getWidth()/(float) holder.videoView.getHeight();
                    float scale=videoRatio/screenRatio;
                    if(scale>=1f){
                        holder.videoView.setScaleX(scale);
                    }else {
                        holder.videoView.setScaleY(1f/scale);
                    }
                }
            });
            holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
        }
        else {
            holder.videoView.setVideoURI(null);
        }


    }
    private Bitmap getUserImage(String encodedImage){
        if (encodedImage == null) {
            // Handle the case when the encoded image is null
            return null;
        }
        byte[] bytes= Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class StroryViewHolder extends RecyclerView.ViewHolder {
         RoundedImageView anh_dai_dien;
         VideoView videoView;
         TextView name,descripttion;
         CardView cardView;
        public StroryViewHolder(@NonNull View itemView) {
            super(itemView);
            anh_dai_dien=itemView.findViewById(R.id.imgProfile);
            name=itemView.findViewById(R.id.textName);
            cardView=itemView.findViewById(R.id.cardview);
            videoView=itemView.findViewById(R.id.videoand);
            descripttion=itemView.findViewById(R.id.textDescripttion);
        }

    }

}
