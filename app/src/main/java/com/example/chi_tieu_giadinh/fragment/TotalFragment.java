package com.example.chi_tieu_giadinh.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chi_tieu_giadinh.R;
import com.example.chi_tieu_giadinh.chitieu.ChitieuViewpagerAdapter;
import com.google.android.material.tabs.TabLayout;


public class TotalFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView tetxt;
    private View mview;

    public TotalFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mview= inflater.inflate(R.layout.fragment_total, container, false);
        tabLayout=mview.findViewById(R.id.tab_layout);
        viewPager=mview.findViewById(R.id.tintuc_viewpaper);
        ChitieuViewpagerAdapter adapter=new ChitieuViewpagerAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        return mview;
    }
}