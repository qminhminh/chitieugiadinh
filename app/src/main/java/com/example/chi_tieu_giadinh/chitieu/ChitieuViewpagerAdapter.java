package com.example.chi_tieu_giadinh.chitieu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ChitieuViewpagerAdapter extends FragmentStatePagerAdapter {
    public ChitieuViewpagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:return new DashBoardFragment();
            case 1:return new ExpenseFragment();
            case 2:return new IncomeFragment();
            default:return new DashBoardFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:return "Tạo ";
            case 1:return "Tiền chi";
            case 2:return "Tiền thu";
            default:return "Tạo";
        }
    }
}
