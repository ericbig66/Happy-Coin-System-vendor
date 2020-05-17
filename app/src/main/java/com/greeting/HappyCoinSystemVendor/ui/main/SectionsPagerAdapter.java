package com.greeting.HappyCoinSystemVendor.ui.main;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.greeting.HappyCoinSystemVendor.EventAttendList;
import com.greeting.HappyCoinSystemVendor.R;
import com.greeting.HappyCoinSystemVendor.RedEnvelopeDiary;
import com.greeting.HappyCoinSystemVendor.SellDiary;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 * 子頁籤設定區，除有註解處其他勿動
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    //陣列內為頁籤名稱(需在strings.xml內定義)
    private static final int[] TAB_TITLES = new int[]{R.string.Sell, R.string.RedEnvelop,R.string.AttendList};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        //將以下三行註解
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
//        return PlaceholderFragment.newInstance(position + 1);
        //加入回傳頁面判斷器(回傳目前所在頁面決定顯示內容)
        switch (position){
            case 0:
                return SellDiary.newInstance();
            case 1:
                return RedEnvelopeDiary.newInstance();
            case 2:
                return EventAttendList.newInstance();
            default:
                Log.v("test","Error while loading fragment");
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        //回傳頁面總數
        return 3;
    }
}