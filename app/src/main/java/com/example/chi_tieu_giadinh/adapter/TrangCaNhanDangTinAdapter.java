package com.example.chi_tieu_giadinh.adapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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
import com.example.chi_tieu_giadinh.model.DangTinModel;
import com.makeramen.roundedimageview.RoundedImageView;
import java.util.ArrayList;


public class TrangCaNhanDangTinAdapter extends RecyclerView.Adapter<TrangCaNhanDangTinAdapter.TrangCaNhanDangTin> {
    ArrayList<DangTinModel> arrayList = new ArrayList<>();
    Context context;

    public TrangCaNhanDangTinAdapter(ArrayList<DangTinModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public TrangCaNhanDangTinAdapter.TrangCaNhanDangTin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dang_tin, parent, false);
        return new TrangCaNhanDangTinAdapter.TrangCaNhanDangTin(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrangCaNhanDangTinAdapter.TrangCaNhanDangTin holder, int position) {
        DangTinModel model=arrayList.get(position);

        if (model.getImgDaidien() != null) {
            holder.img_dai_dien.setImageBitmap(getUserImage(model.getImgDaidien()));
        } else {
            // Xử lý khi giá trị model.getImgDaidien() là null
        }
        if(model.getImgDaidien()==null){
            holder.img_dang_tin.setVisibility(View.GONE);
        }
        holder.name.setText(model.getName());
        holder.mota.setText(model.getEdit());
        if (model.getImgDaidien() != null)  {
            Glide.with(context).load(arrayList.get(position).getImgDangTin()).into(holder.img_dang_tin);
        }else {
            holder.img_dang_tin.setVisibility(View.GONE);
        };

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

     class TrangCaNhanDangTin extends RecyclerView.ViewHolder{
         RoundedImageView img_dai_dien;
         VideoView video;
         TextView name, time, mota, count_like, like, count_comment, count_share, share, comment;
         ImageView icon_like, img_share, img_comne;
         AppCompatImageView close;
         ImageView img_dang_tin;
         ConstraintLayout constraintLayout;
         public TrangCaNhanDangTin(@NonNull View itemView) {
             super(itemView);
             img_dai_dien = itemView.findViewById(R.id.imgProfile);
             video = itemView.findViewById(R.id.iv_video);
             name = itemView.findViewById(R.id.textName);
             time = itemView.findViewById(R.id.textTime);
             mota = itemView.findViewById(R.id.tv_mota);
             count_like = itemView.findViewById(R.id.tv_soluotlike);
             like = itemView.findViewById(R.id.tv_like);
             icon_like = itemView.findViewById(R.id.image_like);
             close = itemView.findViewById(R.id.imgSignOut);
             img_dang_tin = itemView.findViewById(R.id.iv_imag);
             img_share = itemView.findViewById(R.id.image_sent);
             img_comne = itemView.findViewById(R.id.image_comment);
             constraintLayout = itemView.findViewById(R.id.liner);
         }
     }
}
