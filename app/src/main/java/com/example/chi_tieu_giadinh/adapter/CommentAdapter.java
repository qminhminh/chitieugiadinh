package com.example.chi_tieu_giadinh.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.model.CommentModel;
import com.example.chi_tieu_giadinh.model.DangTinModel;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    ArrayList<CommentModel> arrayList=new ArrayList<>();
    Listen listen;

    public CommentAdapter(ArrayList<CommentModel> arrayList, Listen listen) {
        this.arrayList = arrayList;
        this.listen = listen;
    }

    @NonNull
    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment,parent,false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CommentViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CommentModel model=arrayList.get(position);
        holder.name.setText(model.getName());
        holder.bl.setText(model.getTx_luan());
        if (model.getImg() != null) {
            holder.img_daidien.setImageBitmap(getUserImage(model.getImg()));
        } else {
            // Xử lý khi giá trị model.getImgDaidien() là null
        }
        isTime(model);
        holder.date.setText(model.getDatetime());
        holder.thuhoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listen.Comamt(model,position);
            }
        });

    }
    private void isTime(CommentModel model) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String id = model.getId();

        // Lấy ngày giờ hiện tại
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Tạo HashMap chứa dữ liệu cần cập nhật
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constraints.KEY_COLECTION_ADD_COMMENT_DATE, currentDate);

        db.collection(Constraints.KEY_COLECTION_ADD_COMMENT).document(id)
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
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

     class CommentViewHolder extends RecyclerView.ViewHolder{
        RoundedImageView img_daidien;
        TextView name,date,bl;
        ImageView thuhoi;
         public CommentViewHolder(@NonNull View itemView) {
             super(itemView);
             img_daidien=itemView.findViewById(R.id.imgProfile);
             name=itemView.findViewById(R.id.tv_name);
             date=itemView.findViewById(R.id.tv_date);
             bl=itemView.findViewById(R.id.tv_dan_comment);
             thuhoi=itemView.findViewById(R.id.thuhoicomment);
         }
     }
    private Bitmap getUserImage(String encodedImage){
        byte[] bytes= Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
    public interface Listen{
        void Comamt(CommentModel model,int position);
    }
}
