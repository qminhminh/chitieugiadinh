package com.example.chi_tieu_giadinh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.chi_tieu_giadinh.checkwifi.NetworkChangeReceiver;
import com.example.chi_tieu_giadinh.fragment.ViewPaperAdapter;
import com.example.chi_tieu_giadinh.khoangkhac.KhoangKhacImageActivity;
import com.example.chi_tieu_giadinh.taikhoancanhan.AddDangTinActivity;
import com.example.chi_tieu_giadinh.taikhoancanhan.CreateNoticeActivity;
import com.example.chi_tieu_giadinh.taikhoancanhan.CreteEventActivity;
import com.example.chi_tieu_giadinh.taikhoancanhan.MapActivity;
import com.example.chi_tieu_giadinh.taikhoancanhan.TrangCaNhanActivity;
import com.example.chi_tieu_giadinh.utiliti.Constraints;
import com.example.chi_tieu_giadinh.utiliti.Preferencemanager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private Preferencemanager preferencemanager;
    private FirebaseFirestore databse;
   private ViewPager viewpager;
   private BottomNavigationView botoomView;
   private NavigationView navigationView;
   private DrawerLayout drawerLayout;
    Intent intent;
    private NetworkChangeReceiver networkChangeReceiver;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewpager=findViewById(R.id.viewpager);
        botoomView=findViewById(R.id.botoomView);
        navigationView=findViewById(R.id.navigation);
        drawerLayout=findViewById(R.id.drawer_layout);
        preferencemanager=new Preferencemanager(getApplicationContext());
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            // If the device has internet connectivity, proceed with the normal flow
            bottomViewpaer();
            navigationView();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the BroadcastReceiver when the activity is destroyed
        unregisterReceiver(networkChangeReceiver);
    }




    private void navigationView(){
        int tuoi= Integer.parseInt(preferencemanager.getString(Constraints.KEY_DONGHO));
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.mnhome:
                        viewpager.setCurrentItem(0);
                        drawerLayout.closeDrawer(GravityCompat.START); break;
                    case R.id.mnchitieu:
                        viewpager.setCurrentItem(1);
                        drawerLayout.closeDrawer(GravityCompat.START); break;
                    case R.id.mnthongtingiadinh:
                        viewpager.setCurrentItem(2);
                        drawerLayout.closeDrawer(GravityCompat.START); break;
                    case R.id.mnthongbao:
                        viewpager.setCurrentItem(3);
                        drawerLayout.closeDrawer(GravityCompat.START); break;
                    case R.id.mnkhoangkhac:
                        intent=new Intent(MainActivity.this,  KhoangKhacImageActivity.class);
                        startActivities(new Intent[]{intent});
                        drawerLayout.closeDrawer(GravityCompat.START); break;
                    case R.id.mnmap:
                        intent=new Intent(MainActivity.this, MapActivity.class);
                        startActivities(new Intent[]{intent});
                        drawerLayout.closeDrawer(GravityCompat.START); break;
                    case R.id.mntrangcanhan:
                        intent=new Intent(MainActivity.this, TrangCaNhanActivity.class);
                        startActivities(new Intent[]{intent});
                        drawerLayout.closeDrawer(GravityCompat.START); break;
                    case R.id.mndangtin:
                      if (tuoi>=18){
                          intent=new Intent(MainActivity.this, AddDangTinActivity.class);
                          startActivities(new Intent[]{intent});
                          drawerLayout.closeDrawer(GravityCompat.START); break;
                      }
                      else {
                          Toast.makeText(MainActivity.this, "Không đủ tuổi để đăng tin", Toast.LENGTH_SHORT).show();
                          intent=new Intent(MainActivity.this, MainActivity.class);
                          startActivities(new Intent[]{intent});
                      }

                    case R.id.mntaothongbao:
                        intent=new Intent(MainActivity.this, CreateNoticeActivity.class);
                        startActivities(new Intent[]{intent});
                        drawerLayout.closeDrawer(GravityCompat.START); break;
                    case R.id.mntaosukien:
                        intent=new Intent(MainActivity.this, CreteEventActivity.class);
                        startActivities(new Intent[]{intent});
                        drawerLayout.closeDrawer(GravityCompat.START); break;
                }
                return true;
            }
        });
    }
    private void bottomViewpaer(){
        ViewPaperAdapter adapter = new ViewPaperAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewpager.setAdapter(adapter);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                       botoomView.getMenu().findItem(R.id.item_home).setChecked(true);
                        break;
                    case 1:
                        botoomView.getMenu().findItem(R.id.item_total).setChecked(true);
                        break;
                    case 2:
                        botoomView.getMenu().findItem(R.id.item_notice_giadinh).setChecked(true);
                        break;
                    case 3:
                       botoomView.getMenu().findItem(R.id.item_notice).setChecked(true);
                        break;
                    case 4:
                        botoomView.getMenu().findItem(R.id.item_menu).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        botoomView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_home:
                       viewpager.setCurrentItem(0);
                        break;
                    case R.id.item_total:
                        viewpager.setCurrentItem(1);
                        break;
                    case R.id.item_notice_giadinh:
                       viewpager.setCurrentItem(2);
                        break;
                    case R.id.item_notice:
                       viewpager.setCurrentItem(3);
                        break;
                    case R.id.item_menu:
                        viewpager.setCurrentItem(4);
                        break;
                }
                return true;
            }
        });
    }
}