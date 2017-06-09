
package com.lz.music;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cmsc.cmmusic.init.InitCmmInterface;
import com.lz.music.kuyuehui.R;
import com.lz.music.player.MusicPlayer;
import com.lz.music.player.PlayerResponser;
import com.lz.music.ui.MusicPagerAdapter;
import com.lz.music.ui.MusicPlayerPanel;
import com.lz.music.ui.SearchActivity;

public class MusicActivity extends FragmentActivity {
    private ViewPager mPager;

    private static final int PAGE_VIP = 0;
    private static final int PAGE_RECOMMEND = 1;
    private static final int PAGE_TOP = 2;
    private static final int PAGE_FAVORITE = 3;


    private MusicPlayerPanel mPlayerPanel;
    private PlayerResponser mPlayerResponser;

    private RadioGroup mRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_main);

        InitCmmInterface.initSDK(this);
        init();
    }

    private void init() {

        mRadioGroup = (RadioGroup) findViewById(R.id.nav_bar);

        mPlayerPanel = (MusicPlayerPanel) findViewById(R.id.player_panel);
        mPlayerPanel.updatePlayer(MusicPlayer.getInstance().getCurrentMusic());
        mPlayerResponser = new PlayerResponser(mPlayerPanel);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(2);
        MusicPagerAdapter adapter = new MusicPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(adapter);
        mPager.setOnPageChangeListener(mOnPageChangeListener);

        MusicPlayer.getInstance().addMusicPlayerListener(mPlayerResponser);
    }

    public void navigateVip(View v) {
        mPager.setCurrentItem(PAGE_VIP);
    }

    public void navigateRecommend(View v) {
        mPager.setCurrentItem(PAGE_RECOMMEND);
    }

    public void navigateOriginal(View v) {
        mPager.setCurrentItem(PAGE_TOP);
    }

    public void navigateFavorites(View v) {
        mPager.setCurrentItem(PAGE_FAVORITE);
    }

    public void navigateSearch(View v) {
        //TODO gotoSearch
        gotoSearch();
    }

    private void gotoSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    private void selectTabs(int position) {
        switch (position) {
            case PAGE_VIP: {
                mRadioGroup.check(R.id.btn_vip);
                break;
            }
            case PAGE_RECOMMEND: {
                mRadioGroup.check(R.id.btn_recommend);
                break;
            }
            case PAGE_TOP: {
                mRadioGroup.check(R.id.btn_top);
                break;
            }
            case PAGE_FAVORITE: {
                mRadioGroup.check(R.id.btn_favorites);
                break;
            }
            default: {
                //Nothing TODO
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicPlayer.getInstance().removeMusicPlayerListener(mPlayerResponser);
        InitCmmInterface.exitApp(this);
    }

    private OnPageChangeListener mOnPageChangeListener
            = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            selectTabs(position);
        }
    };
}