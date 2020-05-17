package com.greeting.HappyCoinSystemVendor;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.greeting.HappyCoinSystemVendor.ui.main.SectionsPagerAdapter;

/**
 * 此為交易紀錄主檔
 */
public class diary extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_diary);
        //定義連接子頁籤切換器
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        //定義區
        ViewPager viewPager = findViewById(R.id.view_pager);
        TabLayout tabs = findViewById(R.id.tabs);
        FloatingActionButton fab = findViewById(R.id.fab);//此行無用，但不可刪除否則須連動刪除相關物件
        //設定區
        viewPager.setAdapter(sectionsPagerAdapter);//連接主檔與子頁籤
        tabs.setupWithViewPager(viewPager);//初始化個別頁籤
    }

    @Override
    //返回首頁
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(diary.this, Home.class);
        startActivity(intent);
        finish();
    }
}