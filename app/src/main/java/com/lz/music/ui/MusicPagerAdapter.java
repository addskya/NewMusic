
package com.lz.music.ui;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MusicPagerAdapter extends FragmentPagerAdapter {
    private static final int PAGE_COUNT = 3;
    private static final int PAGE_ORIGINAL = 0;
    private static final int PAGE_FAVORITES = 1;
    private static final int PAGE_SEARCH = 2;

    public MusicPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch (position) {
            case PAGE_ORIGINAL:
                return OriginalListFragment.newInstance();
            case PAGE_FAVORITES:
                return FavoritesFragment.newInstance();
            case PAGE_SEARCH:
                return SearchFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}
