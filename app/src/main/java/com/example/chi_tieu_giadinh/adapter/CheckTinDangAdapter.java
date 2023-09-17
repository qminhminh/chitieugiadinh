package com.example.chi_tieu_giadinh.adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.model.CheckTinModel;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class CheckTinDangAdapter extends RecyclerView.Adapter<CheckTinDangAdapter.CheckViewHolder> {
   ArrayList<CheckTinModel> arrayList=new ArrayList<>();
   Listeners listeners;
   Context context;

    public CheckTinDangAdapter(ArrayList<CheckTinModel> arrayList, Listeners listeners,Context context) {
        this.arrayList = arrayList;
        this.listeners = listeners;
        if (this.arrayList == null) {
            this.arrayList = new ArrayList<>();
        }
        this.context=context;
    }

    @NonNull
    @Override
    public CheckTinDangAdapter.CheckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dang_tin_duyet,parent,false);
        return new  CheckTinDangAdapter.CheckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckTinDangAdapter.CheckViewHolder holder, @SuppressLint("RecyclerView") int position) {

        CheckTinModel model=arrayList.get(position);

// xủa lý ảnh đạo diện
        if (model.getImgDaidien() != null) {
            holder.img_dai_dien.setImageBitmap(getUserImage(model.getImgDaidien()));
        } else {
            // Xử lý khi giá trị model.getImgDaidien() là null
        }

        holder.name.setText(model.getName());
        holder.mota.setText(model.getEdit());

        if (model.getImgDaidien() != null)
        {
        Glide.with(context).load(arrayList.get(position).getImgDangTin()).into(holder.img_dang_tin);
        }
        else {
            holder.img_dang_tin.setVisibility(View.GONE);
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
                //  holder.like.setTextColor(ContextCompat.getColor(context, R.color.purple_200));
            }
        });
        holder.count_like.setText(String.valueOf(model.getCount()));
// xóa bài đăng
        // lấy time đăng tin
        isTime(model);
        holder.time.setText(model.getTime());
        holder.img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //listeners.CountListeners(position);
            }
        });
        holder.img_comne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //listeners.ComentListensers(position);
            }
        });
        holder.duyetsuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listeners.DuyetListeners(model);
            }
        });

        holder.duyetfalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(position);
            }
        });

    }
    private void isTime(CheckTinModel model) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String id = model.getId();

        // Lấy ngày giờ hiện tại
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Tạo HashMap chứa dữ liệu cần cập nhật
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constraints.KEY_COLECTION_TIME, currentDate);

        db.collection(Constraints.KEY_COLLECTIONS_CHECK_TIN).document(id)
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

    // xóa khỏi firebase khi clikck vào
    public void deleteItem(int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CheckTinModel model = arrayList.get(position);
        // Toast.makeText(mContext, String.valueOf(mCartModelArrayList.get(position).getChosename()), Toast.LENGTH_SHORT).show();
        if (model != null && arrayList != null) {
            String id = model.getId();
            db.collection(Constraints.KEY_COLLECTIONS_CHECK_TIN).document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

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
        return arrayList.size();
    }

   class CheckViewHolder extends RecyclerView.ViewHolder{
       RoundedImageView img_dai_dien;
       VideoView video;
       TextView name,time,mota,count_like,like,count_comment,count_share,share,comment;
       ImageView icon_like,img_share,img_comne;
       AppCompatImageView close;
       ImageView img_dang_tin;
       ConstraintLayout constraintLayout;
       TextView duyetsuccess,duyetfalse;
       public CheckViewHolder(@NonNull View itemView) {
           super(itemView);
           duyetsuccess=itemView.findViewById(R.id.check_duytin);
           duyetfalse=itemView.findViewById(R.id.check_loaibo);
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
    private Bitmap getUserImage(String encodedImage){
        if (encodedImage == null) {
            // Handle the case when the encoded image is null
            return null;
        }
        byte[] bytes= Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
    public interface Listeners{
        void DuyetListeners(CheckTinModel model);
    }
}
