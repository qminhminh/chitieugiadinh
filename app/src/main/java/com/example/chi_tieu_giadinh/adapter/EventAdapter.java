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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.model.CreateEventModel;
import com.makeramen.roundedimageview.RoundedImageView;
import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder> {
    ArrayList<CreateEventModel> arrayList;
    Listeners listeners;


    public EventAdapter(ArrayList<CreateEventModel> arrayList, Listeners listeners) {
        this.arrayList = arrayList;
        this.listeners = listeners;
    }

    @NonNull
    @Override
    public EventAdapter.EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_create_event,parent,false);
        return new EventAdapter.EventHolder(view);
    }
    private String previousDate = "";
    @Override
    public void onBindViewHolder(@NonNull EventAdapter.EventHolder holder, @SuppressLint("RecyclerView") int position) {

        CreateEventModel model=arrayList.get(position);
        if (model.getImg() != null) {
            holder.imageView.setImageBitmap(getUserImage(model.getImg()));
        } else {
            holder.imageView.setImageBitmap(null);
        }
        holder.tv_Name.setText(model.getName());

      //  boolean isCurrentUserPoster = preferencemanager.getString(Constraints.KEY_NAME).equals(model.getName());
        if(model.getDate()!=null||model.getTime()!=null){
            holder.timeanddate.setText(model.getDate()+" "+model.getTime());
        }
        else {

            holder.timeanddate.setText(null);
        }

        if(model.getNoidung()!=null){
            holder.dateil.setText(model.getNoidung());
        }else {
            holder.dateil.setText(null);
        }
        if(model.getGhichu()!=null){
            holder.ghichu.setText(model.getGhichu());
        }
        else {
            holder.ghichu.setText(null);
        }
        // su kien
        holder.huy.setVisibility(View.INVISIBLE);
        holder.thamgia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listeners.EventListeners(model,position);
                holder.thamgia.setVisibility(View.INVISIBLE);
                holder.khongthamgia.setVisibility(View.GONE);
                holder.tb_tg.setVisibility(View.VISIBLE);
                holder.tb_ktg.setVisibility(View.GONE);
                holder.huy.setVisibility(View.VISIBLE);
            }
        });
        holder.khongthamgia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.thamgia.setVisibility(View.GONE);
                holder.khongthamgia.setVisibility(View.INVISIBLE);
                holder.tb_ktg.setVisibility(View.VISIBLE);
                holder.tb_tg.setVisibility(View.GONE);
                holder.huy.setVisibility(View.VISIBLE);
            }
        });
        holder.huy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.thamgia.setVisibility(View.VISIBLE);
                holder.khongthamgia.setVisibility(View.VISIBLE);
                holder.huy.setVisibility(View.INVISIBLE);
                listeners.DeleteLiset(model,position);
            }
        });
        holder.imageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listeners.CloseListeners(model,position);
            }
        });
        if (!model.getDate().equals(previousDate)) {
            holder.ata.setVisibility(View.VISIBLE); // Hiển thị ngày tháng năm
            holder.ata.setText(model.getDate()); // Gán giá trị ngày tháng năm

            previousDate = model.getDate(); // Cập nhật ngày trước đó
        } else {
            holder.ata.setVisibility(View.GONE); // Ẩn ngày tháng năm nếu giống với ngày trước đó
        }

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listeners.ClickItem(model,position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class EventHolder extends RecyclerView.ViewHolder{
        RoundedImageView imageView;
        TextView tv_Name;
        TextView timeanddate;
        TextView dateil;
        TextView thamgia;
        TextView khongthamgia;
        TextView tb_tg;
        TextView tb_ktg;
        TextView ghichu;
        TextView huy;
        TextView ata;
        ImageView imageIcon;
        ConstraintLayout constraintLayout;
        public EventHolder(@NonNull View itemView) {
            super(itemView);
            imageIcon=itemView.findViewById(R.id.close);
            constraintLayout=itemView.findViewById(R.id.liner);
            ata=itemView.findViewById(R.id.dscs);
            imageView=itemView.findViewById(R.id.imgProfile);
            tv_Name=itemView.findViewById(R.id.textName);
            timeanddate=itemView.findViewById(R.id.textTimeandDate);
            dateil=itemView.findViewById(R.id.textDetail);
            thamgia=itemView.findViewById(R.id.tv_thamgia);
            khongthamgia=itemView.findViewById(R.id.tv_khongthamgia);
            tb_tg=itemView.findViewById(R.id.tv_tt);
            tb_ktg=itemView.findViewById(R.id.tv_kott);
            ghichu=itemView.findViewById(R.id.textghichu);
            huy=itemView.findViewById(R.id.tv_cancle);
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
        void EventListeners(CreateEventModel model,int position);
        void DeleteLiset(CreateEventModel model,int position);
        void ClickItem(CreateEventModel model,int position);
        void CloseListeners(CreateEventModel model,int position);

    }
}
