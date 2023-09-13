package com.example.chi_tieu_giadinh.profilevent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class EventViewpagerAdapter extends FragmentStatePagerAdapter {
    public EventViewpagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:return new ChitietEvntFragment();
            case 1:return new NguoiThamgiaFragment();
            default:return new ChitietEvntFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:return "Chi tiết";
            case 1:return "Người tham gia";
            default:return "Chi tiết";
        }
    }
}
