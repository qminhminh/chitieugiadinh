package com.example.chi_tieu_giadinh.taikhoancanhan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import com.google.android.gms.maps.GoogleMap;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chi_tieu_giadinh.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private Preferencemanager preferencemanager;
    private TextView textView;
    private LinearLayout layout;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        preferencemanager=new Preferencemanager(getApplicationContext());
        layout=findViewById(R.id.linear);
        textView=findViewById(R.id.text);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        FirebaseAuth auth=FirebaseAuth.getInstance();

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address =preferencemanager.getString(Constraints.KEY_NOIO);
                String name=preferencemanager.getString(Constraints.KEY_NAME);

                Geocoder geocoder = new Geocoder(MapActivity.this);
                try {
                    List<Address> addresses = geocoder.getFromLocationName(address, 1);
                    if (!addresses.isEmpty()) {
                        Address firstAddress = addresses.get(0);
                        double latitude = firstAddress.getLatitude();
                        double longitude = firstAddress.getLongitude();

                        LatLng location = new LatLng(latitude, longitude);
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(location)
                                .title(name)
                                .snippet(address); // Để trống nội dung snippet
                        googleMap.addMarker(markerOptions);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17f));

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Map<String, Object> locationData = new HashMap<>();
                        locationData.put(Constraints.KEY_LOCATION_ADDRESS, address);
                        locationData.put(Constraints.KEY_LOCATION_LATITIUE, latitude);
                        locationData.put(Constraints.KEY_LOCATION_LONGTITUDE, longitude);
                        locationData.put(Constraints.KEY_NAME,preferencemanager.getString(Constraints.KEY_NAME));

                        db.collection(Constraints.KEY_COLLECTIONS_LOCATION).document(auth.getCurrentUser().getUid())
                                .set(locationData)
                                .addOnSuccessListener(documentReference -> Toast.makeText(MapActivity.this, "", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(MapActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(MapActivity.this, "Không tìm thấy địa chỉ.", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constraints.KEY_COLLECTIONS_LOCATION)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get latitude, longitude, and address from the document
                            double latitude = document.getDouble(Constraints.KEY_LOCATION_LATITIUE);
                            double longitude = document.getDouble(Constraints.KEY_LOCATION_LONGTITUDE);
                            String address = document.getString(Constraints.KEY_LOCATION_ADDRESS);
                            String name= document.getString(Constraints.KEY_NAME);

                            // Create LatLng object
                            LatLng location = new LatLng(latitude, longitude);

                            // Add marker to the map
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(location)
                                    .title(name)
                                    .snippet(address)
                                    ; // Để trống nội dung snippet
                            googleMap.addMarker(markerOptions);
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17f));
                        }
                    } else {
                        Log.d("Firebase", "Error getting documents: ", task.getException());
                    }
                });
    }
}