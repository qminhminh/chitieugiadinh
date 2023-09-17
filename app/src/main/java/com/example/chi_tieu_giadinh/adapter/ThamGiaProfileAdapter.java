package com.example.chi_tieu_giadinh.adapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.model.ThamGiaProfile;
import com.makeramen.roundedimageview.RoundedImageView;
import java.util.ArrayList;

public class ThamGiaProfileAdapter extends RecyclerView.Adapter<ThamGiaProfileAdapter.ThamGiaViewHolder> {
    ArrayList<ThamGiaProfile> arrayList;

    public ThamGiaProfileAdapter(ArrayList<ThamGiaProfile> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ThamGiaProfileAdapter.ThamGiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_thamgia,parent,false);
        return new ThamGiaProfileAdapter.ThamGiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThamGiaProfileAdapter.ThamGiaViewHolder holder, int position) {
         ThamGiaProfile model=arrayList.get(position);
         if(model.getImg()!=null){
             holder.imageView.setImageBitmap(getUserImage(model.getImg()));
         }
         else {
             holder.imageView.setImageBitmap(null);
         }
         if (model.getName()!=null){
             holder.textView_nmae.setText(model.getName());
         }else {
             holder.textView_nmae.setText(null);
         }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ThamGiaViewHolder extends RecyclerView.ViewHolder{
        RoundedImageView imageView;
        TextView textView_nmae;
        public ThamGiaViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imgProfile);
            textView_nmae=itemView.findViewById(R.id.tv_profileuserthamgia);
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
}
