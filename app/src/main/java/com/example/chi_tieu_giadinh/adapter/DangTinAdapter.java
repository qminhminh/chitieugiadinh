package com.example.chi_tieu_giadinh.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.model.DangTinModel;
import com.example.chi_tieu_giadinh.model.UserModel;
import com.example.chi_tieu_giadinh.taikhoancanhan.CommentActivity;
import com.example.chi_tieu_giadinh.taikhoancanhan.ProfileActivity;
import com.example.chi_tieu_giadinh.taikhoancanhan.TrangCaNhanActivity;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DangTinAdapter extends RecyclerView.Adapter<DangTinAdapter.DangViewHolder> {
    ArrayList<DangTinModel> arrayList=new ArrayList<>();
    Listeners listeners;
    Context context;


    public DangTinAdapter(ArrayList<DangTinModel> arrayList, Listeners listeners,Context context) {
        this.arrayList = arrayList;

        this.listeners = listeners;
        if (this.arrayList == null) {
            this.arrayList = new ArrayList<>();
        }
        this.context=context;
    }

    @NonNull
    @Override
    public DangTinAdapter.DangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dang_tin,parent,false);
        return new DangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DangTinAdapter.DangViewHolder holder, @SuppressLint("RecyclerView") int position) {
      DangTinModel model=arrayList.get(position);
        if (model.getImgDaidien() != null)
        {
            Glide.with(context).load(arrayList.get(position).getImgDangTin()).into(holder.img_dang_tin);
        }
        else {
            holder.img_dang_tin.setVisibility(View.GONE);
        }
// xủa lý ảnh đạo diện
        if (model.getImgDaidien() != null) {
            holder.img_dai_dien.setImageBitmap(getUserImage(model.getImgDaidien()));
        } else {
            // Xử lý khi giá trị model.getImgDaidien() là null
        }
        if(model.getImgDangTin() ==null){
          //  holder.img_dang_tin.setVisibility(View.GONE);
        }
        holder.name.setText(model.getName());
        holder.mota.setText(model.getEdit());
        // xủa lý vidoe và ảnh
        if (model.getVideo() != null && !model.getVideo().isEmpty()) {
          //  holder.video.setVideoURI(Uri.parse(model.getVideo()));
            holder.video.setVideoPath(model.getVideo());
            holder.video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    float videoRatio=mediaPlayer.getVideoWidth()/(float) mediaPlayer.getVideoHeight();
                    float screenRatio=holder.video.getWidth()/(float) holder.video.getHeight();
                    float scale=videoRatio/screenRatio;
                    if(scale>=1f){
                        holder.video.setScaleX(scale);
                    }else {
                        holder.video.setScaleY(1f/scale);
                    }
                }
            });
            holder.video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
         //   holder.img_dang_tin.setVisibility(View.GONE);
            holder.video.setVisibility(View.VISIBLE);

        } else {
            if(model.getImgDaidien()==null){
            //    holder.img_dang_tin.setVisibility(View.GONE);
            }else {
              //  holder.img_dang_tin.setImageBitmap(getUserImage(model.getImgDangTin()));
              //  holder.img_dang_tin.setVisibility(View.VISIBLE);
                holder.video.setVisibility(View.GONE);
            }
        }

// xủa lý so lượt like
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.icon_like.setColorFilter(Color.BLUE);
               // holder.like.setTextColor(ContextCompat.getColor(context, R.color.purple_200));
            }
        });
// xủa lý icon like
        holder.icon_like.setOnClickListener(new View.OnClickListener() {
            boolean isLiked = false;
            @Override
            public void onClick(View view) {
                if (isLiked) {
                    // Nếu đã được like, thực hiện count-1 và đổi màu icon
                    model.setCount(model.getCount() - 1);
                    holder.count_like.setText(String.valueOf(model.getCount()));
                    holder.icon_like.setImageResource(R.drawable.baseline_emoji_emotions_24);
                    isLiked = false;

                } else {
                    // Nếu chưa được like, thực hiện count+1 và đổi màu icon
                    model.setCount(model.getCount() + 1);
                    holder.count_like.setText(String.valueOf(model.getCount()));
                    holder.icon_like.setImageResource(R.drawable.blueemoji_emotions_24);
                    isLiked = true;

                }
                updateItem(model);
              //  holder.like.setTextColor(ContextCompat.getColor(context, R.color.purple_200));
            }
        });
        holder.count_like.setText(String.valueOf(model.getCount()));
// xóa bài đăng
        holder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listeners.CloseListeners(model,position);
            }
        });
        // lấy time đăng tin
        isTime(model);
        holder.time.setText(model.getTime());
        holder.img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listeners.CountListeners(position);
            }
        });
        holder.img_comne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               listeners.ComentListensers(position);
            }
        });

    }
// xủa lý đăng thời gian
    private void isTime(DangTinModel model) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String id = model.getId();

        // Lấy ngày giờ hiện tại
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Tạo HashMap chứa dữ liệu cần cập nhật
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constraints.KEY_COLECTION_TIME, currentDate);

        db.collection(Constraints.KEY_COLECTION_ADD_TIN).document(id)
                .update(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Thành công, xử lý các tác vụ khác nếu cần
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý lỗi nếu cần
                    }
                });
    }

// cập nhật số lượng like
    private void updateItem(DangTinModel model){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String id = model.getId();

        // Tạo HashMap chứa dữ liệu cần cập nhật
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constraints.KEY_COLECTION_ADD_COUNT, model.getCount());

        db.collection(Constraints.KEY_COLECTION_ADD_TIN).document(id)
                .update(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Thành công, xử lý các tác vụ khác nếu cần
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý lỗi nếu cần
                    }
                });
    }
    // xóa khỏi firebase khi clikck vào
    public void deleteItem(int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DangTinModel model = arrayList.get(position);
       // Toast.makeText(mContext, String.valueOf(mCartModelArrayList.get(position).getChosename()), Toast.LENGTH_SHORT).show();
        if (model != null && arrayList != null) {
            String id = model.getId();
            db.collection(Constraints.KEY_COLECTION_ADD_TIN).document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                           // Toast.makeText(context, "Sucess", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                           // Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
                            Log.e("DeleteItem", "Failed to delete item", e);
                        }
                    });
            arrayList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, arrayList.size());
        } else {
            //Toast.makeText(context, "Model or ID is null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        if (arrayList != null && !arrayList.isEmpty()) {
            return arrayList.size();
        } else {
            return 0;
        }
    }

    class DangViewHolder extends RecyclerView.ViewHolder{
         RoundedImageView img_dai_dien;
         VideoView video;
         TextView name,time,mota,count_like,like,count_comment,count_share,share,comment;
         ImageView icon_like,img_share,img_comne;
         AppCompatImageView close;
         ImageView img_dang_tin;
         ConstraintLayout constraintLayout;
         public DangViewHolder(@NonNull View itemView) {
             super(itemView);
             img_dai_dien=itemView.findViewById(R.id.imgProfile);
             video=itemView.findViewById(R.id.iv_video);
             name=itemView.findViewById(R.id.textName);
             time=itemView.findViewById(R.id.textTime);
             mota=itemView.findViewById(R.id.tv_mota);
             count_like=itemView.findViewById(R.id.tv_soluotlike);
             like=itemView.findViewById(R.id.tv_like);
             icon_like=itemView.findViewById(R.id.image_like);
             close=itemView.findViewById(R.id.imgSignOut);
             img_dang_tin=itemView.findViewById(R.id.iv_imag);
             img_share=itemView.findViewById(R.id.image_sent);
             img_comne=itemView.findViewById(R.id.image_comment);
             constraintLayout=itemView.findViewById(R.id.liner);

         }
     }
// xủa lý ảnh
    private Bitmap getUserImage(String encodedImage){
        if (encodedImage == null) {
            // Handle the case when the encoded image is null
            return null;
        }
        byte[] bytes= Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
    public interface Listeners{
       void CountListeners(int pos);
       void ComentListensers(int pos);
       void TrangcanhanListener(int pos);
       void CloseListeners(DangTinModel model,int positon);

    }
}
