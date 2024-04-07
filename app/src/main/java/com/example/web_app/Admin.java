package com.example.web_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.web_app.databinding.ActivityAdminBinding;

import com.example.web_app.fragment.OneFragment;
import com.example.web_app.fragment.TwoFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.web_app.fragmentadmin.OneAdmin;
import com.example.web_app.fragmentadmin.TwoAdmin;
import com.example.web_app.stats.FragmentStats;
import com.example.web_app.stats.StatsActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class Admin extends AppCompatActivity {

    private ActivityAdminBinding binding;
    private Admin activity;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin);
        activity = this;

        initView();

        ImageView imageViewStats = findViewById(R.id.imageView_stats);
        imageViewStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin.this, StatsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        setupViewPager(binding.viewPager);


        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    tab.setText(adapter.mFragmentTitleList.get(position));
                }).attach();

        for (int i = 0; i < binding.tabLayout.getTabCount(); i++) {
            TextView tv = (TextView) LayoutInflater.from(activity)
                    .inflate(R.layout.custom_tab, null);
            binding.tabLayout.getTabAt(i).setCustomView(tv);
        }
    }


    private void setupViewPager(ViewPager2 viewPager) {
        adapter = new ViewPagerAdapter(activity.getSupportFragmentManager(),
                activity.getLifecycle());
        adapter.addFragment(new OneAdmin(), "Non traité");
        adapter.addFragment(new TwoAdmin(), "En cours ");

        adapter.notifyDataSetChanged();

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
    }



    class ViewPagerAdapter extends FragmentStateAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @NonNull
        @NotNull
        @Override
        public Fragment createFragment(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getItemCount() {
            return mFragmentList.size();
        }
    }
}