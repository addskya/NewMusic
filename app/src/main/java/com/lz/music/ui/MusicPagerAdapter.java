
package com.lz.music.ui;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MusicPagerAdapter extends FragmentPagerAdapter {

    private static final int PAGE_VIP = 0;
    private static final int PAGE_RECOMMEND = 1;
    private static final int PAGE_TOP = 2;
    private static final int PAGE_FAVORITE = 3;


    private static final int PAGE_COUNT = 4;

    public MusicPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch (position) {
            case PAGE_VIP:
                return VipFragment.newInstance();
            case PAGE_RECOMMEND:
                return RecommendFragment.newInstance();
            case PAGE_TOP:
                return OriginalListFragment.newInstance();
            case PAGE_FAVORITE:
                return FavoritesFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}
