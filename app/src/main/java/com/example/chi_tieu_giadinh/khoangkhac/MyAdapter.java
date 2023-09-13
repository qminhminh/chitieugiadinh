package com.example.chi_tieu_giadinh.khoangkhac;



import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.taikhoancanhan.ImageProfileActivity;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    ArrayList<DataClass> dataList;
    Context context;

    public MyAdapter(ArrayList<DataClass> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.staggere_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getImageURL()).into(holder.staggeredImages);
        DataClass dataClass=dataList.get(position);
        holder.staggeredImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ImageProfileActivity.class);
                intent.putExtra("imaeg", dataClass.getImageURL());
                intent.putExtra("caption", dataClass.getCaption());
                view.getContext().startActivity(intent);

            }
        });
        holder.imagedowload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadAndSaveImage(dataClass.getImageURL(), dataClass.getCaption());
            }
        });
    }
    private void downloadAndSaveImage(String imageUrl, String caption) {
        // Thực hiện việc tải xuống ảnh và lưu trữ vào bộ sưu tập
        // Ở đây bạn cần sử dụng thư viện tải xuống ảnh (ví dụ: Picasso, Glide) để tải xuống ảnh từ imageUrl.

        // Sau khi tải xuống thành công, bạn có thể lưu ảnh vào bộ sưu tập.
        // Ở đây tôi sử dụng Glide để tải xuống ảnh, bạn có thể sử dụng thư viện tùy chỉnh khác nếu muốn.

        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // Lưu ảnh vào bộ sưu tập
                        saveImageToGallery(resource, caption);
                    }
                });
    }

    private void saveImageToGallery(Bitmap bitmap, String caption) {
        // Lưu ảnh vào bộ sưu tập trên thiết bị
        String fileName = "Image_" + System.currentTimeMillis() + ".jpg";
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Camera/" + fileName;

        try {
            FileOutputStream outStream = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            // Thông báo cho hệ thống rằng đã thêm ảnh mới vào bộ sưu tập
            MediaStore.Images.Media.insertImage(context.getContentResolver(), filePath, fileName, caption);

            Toast.makeText(context, "Đã lưu ảnh vào bộ sưu tập.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Lỗi khi lưu ảnh vào bộ sưu tập.", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        RoundedImageView staggeredImages;
        ImageView imagedowload;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            staggeredImages = itemView.findViewById(R.id.staggeredImages);
            imagedowload=itemView.findViewById(R.id.downloadIcon);
        }
    }
}