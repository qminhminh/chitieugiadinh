package com.example.chi_tieu_giadinh.taikhoancanhan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.databinding.ActivityImageProfileBinding;
import com.example.chi_tieu_giadinh.khoangkhac.KhoangKhacImageActivity;

public class ImageProfileActivity extends AppCompatActivity {

    ActivityImageProfileBinding binding;
    Bundle bundle;
    float scale = 1f;
    float minScale = 1f;
    float maxScale = 3f;
    float previousX = 0f;
    float previousY = 0f;
    float previousScale = 0f;
    float initialDistance = 0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityImageProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bundle = getIntent().getExtras();
        if (bundle != null) {
            String img = bundle.getString("imaeg");
            String caption = bundle.getString("caption");



            Glide.with(this)
                    .load(img)
                    .into(binding.imgProfile);
            binding.textView.setText(caption);
        }
        binding.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ImageProfileActivity.this, KhoangKhacImageActivity.class));
            }
        });
        setListensers();
    }
    private float getDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }
    private void setListensers(){

        Matrix matrix = new Matrix();
        binding.imgProfile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        previousX = x;
                        previousY = y;
                        previousScale = scale;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dx = x - previousX;
                        float dy = y - previousY;

                        if (event.getPointerCount() == 2) {
                            // Nếu có 2 ngón tay, tính toán độ phóng to và thu nhỏ ảnh
                            if (initialDistance == 0f) {
                                initialDistance = getDistance(event);
                            }
                            float currentDistance = getDistance(event);
                            scale = previousScale * (currentDistance / initialDistance);
                            scale = Math.max(minScale, Math.min(scale, maxScale));

                            matrix.setScale(scale, scale);
                            binding.imgProfile.setImageMatrix(matrix);
                        } else {
                            // Nếu chỉ có 1 ngón tay, tính toán di chuyển ảnh
                            matrix.postTranslate(dx, dy);
                            binding.imgProfile.setImageMatrix(matrix);
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        // Reset giá trị initialDistance khi ngón tay nhấc lên
                        initialDistance = 0f;
                        break;
                }

                return true;
            }
        });
    }
}