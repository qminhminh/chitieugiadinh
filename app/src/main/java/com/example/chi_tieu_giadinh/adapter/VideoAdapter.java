package com.example.chi_tieu_giadinh.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.model.ModelStrory;


import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder>{
    private List<ModelStrory> videoItems;
    private Context context;

int count=0;

    public VideoAdapter(List<ModelStrory> videoItems, Context context) {
        this.videoItems = videoItems;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contanier_video,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
       holder.setvideoData(videoItems.get(position));
    }

    @Override
    public int getItemCount() {
        return videoItems.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder{
         VideoView videoView;
         TextView textVideoTitle,texVidepDescription,textCountHeart;
         ImageView imageView;
         ProgressBar progressBar;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView=itemView.findViewById(R.id.videoView);
            textVideoTitle=itemView.findViewById(R.id.textVideoTitle);
            texVidepDescription=itemView.findViewById(R.id.textVideoDescitt);
            progressBar=itemView.findViewById(R.id.videoProgressBar);

        }

        void setvideoData(ModelStrory videoItem){
            int count=0;
            textVideoTitle.setText(videoItem.getName());
            texVidepDescription.setText(videoItem.getText());
            videoView.setVideoPath(videoItem.getVideo());
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    progressBar.setVisibility(View.GONE);
                    mediaPlayer.start();
                    float videoRatio=mediaPlayer.getVideoWidth()/(float) mediaPlayer.getVideoHeight();
                    float screenRatio=videoView.getWidth()/(float) videoView.getHeight();
                    float scale=videoRatio/screenRatio;
                    if(scale>=1f){
                        videoView.setScaleX(scale);
                    }else {
                        videoView.setScaleY(1f/scale);
                    }

                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
        }

    }
    

}
