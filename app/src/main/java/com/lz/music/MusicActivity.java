
package com.lz.music;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.Button;

import com.cmsc.cmmusic.init.InitCmmInterface;
import com.lz.music.kuyuehui.R;
import com.lz.music.player.MusicPlayer;
import com.lz.music.player.PlayerResponser;
import com.lz.music.ui.MusicPagerAdapter;
import com.lz.music.ui.MusicPlayerPanel;

public class MusicActivity extends FragmentActivity {
    private ViewPager mPager;

    private Button mOriginal;
    private Button mFavorites;
    private Button mSearch;

    private MusicPlayerPanel mPlayerPanel;
    private PlayerResponser mPlayerResponser;

    private int mSelectedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_main);

        InitCmmInterface.initSDK(this);
        init();
    }

    private void init() {
        mOriginal = (Button) findViewById(R.id.btn_original);
        mFavorites = (Button) findViewById(R.id.btn_favorites);
        mSearch = (Button) findViewById(R.id.btn_search);

        mPlayerPanel = (MusicPlayerPanel) findViewById(R.id.player_panel);
        mPlayerPanel.updatePlayer(MusicPlayer.getInstance().getCurrentMusic());
        mPlayerResponser = new PlayerResponser(mPlayerPanel);

        mSelectedColor = getResources().getColor(R.color.tab_selected_text_color);
        mOriginal.setTextColor(mSelectedColor);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(2);
        MusicPagerAdapter adapter = new MusicPagerAdapter(this, getSupportFragmentManager());
        mPager.setAdapter(adapter);
        mPager.setOnPageChangeListener(mOnPageChangeListener);

        MusicPlayer.getInstance().addMusicPlayerListener(mPlayerResponser);
    }

    public void navigateOriginal(View v) {
        mPager.setCurrentItem(0);
    }

    public void navigateFavorites(View v) {
        mPager.setCurrentItem(1);
    }

    public void navigateSearch(View v) {
        mPager.setCurrentItem(2);
    }

    private void selectTabs(int position) {
        switch (position) {
            case 0:
                mOriginal.setTextColor(mSelectedColor);
                mOriginal.setBackgroundResource(R.drawable.tab_background);
                mFavorites.setTextColor(getResources().getColor(R.color.tab_unselected_text_color));
                mFavorites.setBackgroundDrawable(null);
                mSearch.setTextColor(getResources().getColor(R.color.tab_unselected_text_color));
                mSearch.setBackgroundDrawable(null);
                break;
            case 1:
                mOriginal.setTextColor(getResources().getColor(R.color.tab_unselected_text_color));
                mOriginal.setBackgroundDrawable(null);
                mFavorites.setTextColor(mSelectedColor);
                mFavorites.setBackgroundResource(R.drawable.tab_background);
                mSearch.setTextColor(getResources().getColor(R.color.tab_unselected_text_color));
                mSearch.setBackgroundDrawable(null);
                break;
            case 2:
                mOriginal.setTextColor(getResources().getColor(R.color.tab_unselected_text_color));
                mOriginal.setBackgroundDrawable(null);
                mFavorites.setTextColor(getResources().getColor(R.color.tab_unselected_text_color));
                mFavorites.setBackgroundDrawable(null);
                mSearch.setTextColor(mSelectedColor);
                mSearch.setBackgroundResource(R.drawable.tab_background);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicPlayer.getInstance().removeMusicPlayerListener(mPlayerResponser);
        InitCmmInterface.exitApp(this);
    }

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            selectTabs(position);
        }
    };
}