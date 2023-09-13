package com.example.chi_tieu_giadinh.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ViewPaperAdapter extends FragmentStatePagerAdapter {
    public ViewPaperAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:return new HomeFragment();
            case 1:return new TotalFragment();
            case 2:return new FamilyFragment();
            case 3:return new NoticeFragment();
            case 4:return new MenuFragment();
            default:return new HomeFragment();
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}
