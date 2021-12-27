package com.example.instaclone.homepage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.instaclone.Profile.MyProfileFragment;
import com.example.instaclone.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class HomepageActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.pager);
        setViewPager();
        setTabIcons();

    }

    private void setTabIcons(){
        tabLayout.getTabAt(0).setIcon(R.drawable.home_icon);
        tabLayout.getTabAt(1).setIcon(R.drawable.profile_icon);
    }

    private void setViewPager(){

        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(HomepageFragment.newInstance());
        fragments.add(MyProfileFragment.newInstance("1","2"));
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),fragments);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }
}