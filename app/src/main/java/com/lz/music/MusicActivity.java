
package com.lz.music;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cmsc.cmmusic.init.InitCmmInterface;
import com.lz.music.kuyuehui.R;
import com.lz.music.player.MusicPlayer;
import com.lz.music.player.PlayerResponser;
import com.lz.music.ui.MusicPagerAdapter;
import com.lz.music.ui.MusicPlayerPanel;
import com.lz.music.ui.PermissionsDialog;
import com.lz.music.ui.SearchActivity;

import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends FragmentActivity {
    private ViewPager mPager;
    private static final int REQUEST_CODE_PERMISSION = 0x10;

    private static final int PAGE_VIP = 0;
    private static final int PAGE_RECOMMEND = 1;
    private static final int PAGE_TOP = 2;
    private static final int PAGE_FAVORITE = 3;


    private MusicPlayerPanel mPlayerPanel;
    private PlayerResponser mPlayerResponser;

    private RadioGroup mRadioGroup;
    private View mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_main);

        InitCmmInterface.initSDK(this);
        init();
        handlePermissions();
    }

    private void init() {

        mRadioGroup = (RadioGroup) findViewById(R.id.nav_bar);
        mIndicator = findViewById(R.id.indicator);
        prepareIndicator();
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

    private void prepareIndicator() {
        mIndicator.setVisibility(View.VISIBLE);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        ViewGroup.LayoutParams params = mIndicator.getLayoutParams();
        params.width = (screenWidth - 8 * 20) / 4;
        mIndicator.setLayoutParams(params);
        ViewGroup.MarginLayoutParams margin = (ViewGroup.MarginLayoutParams) params;
        margin.leftMargin = (screenWidth - params.width * 4) / 8;
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

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            final ViewGroup container = mRadioGroup;
            final int pageWidth = container.getMeasuredWidth();
            final int childCount = container.getChildCount();
            if (childCount <= 0) {
                return;
            }
            final View firstView = container.getChildAt(0);
            if (firstView == null) {
                return;
            }
            final int childWidth = firstView.getMeasuredWidth();
            float offset = (pageWidth * 1.0f - childWidth * childCount) / (childCount - 1) + childWidth;
            mIndicator.setTranslationX((position + positionOffset) * offset);
        }
    };

    @TargetApi(Build.VERSION_CODES.M)
    private void handlePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.WRITE_SETTINGS,
            };
            requestPermissions(permissions, REQUEST_CODE_PERMISSION);
        } else {
            showPermissionDialog();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        handlePermissionsResult(permissions, grantResults);
    }

    private void handlePermissionsResult(String[] permissions,
                                         int[] grantResults) {
        final int requestPermissionLength = permissions.length;
        List<String> requestFailurePermissionsList = new ArrayList<String>(1);
        for (int index = 0; index < requestPermissionLength; index++) {
            String permission = permissions[index];
            boolean isPermissionGranted = grantResults[index] == PackageManager.PERMISSION_GRANTED;
            if (!isPermissionGranted) {
                requestFailurePermissionsList.add(permission);
            }
        }
        if (requestFailurePermissionsList.size() > 0) {
            String[] requestFailurePermissions = new String[requestFailurePermissionsList.size()];
            requestFailurePermissionsList.toArray(requestFailurePermissions);
            handleRequestPermissionsFailure(requestFailurePermissions);
        }
    }

    /**
     * handle permissions request failure
     *
     * @param requestFailurePermissions the request failure permissions
     */
    private void handleRequestPermissionsFailure(String[] requestFailurePermissions) {
        onRequestPermissionsFailure(requestFailurePermissions);
    }

    /**
     * Call Back when the specified permission request failure
     *
     * @param requestFailurePermissions the request permissions
     */
    protected void onRequestPermissionsFailure(String[] requestFailurePermissions) {
        Toast.makeText(this, "权限请求失败,部分功能可能无法正常使用.", Toast.LENGTH_SHORT).show();
    }

    private void showPermissionDialog() {
        final SharedPreferences sp = getSharedPreferences("music_pref", Context.MODE_PRIVATE);
        if (sp.getBoolean("permissions_tips", false)) {
            return;
        }

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle(R.string.text_permission)
                .setMessage(R.string.text_request_permissions)
                .setPositiveButton(R.string.text_agree,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sp.edit().putBoolean("permissions_allow", true)
                                        .putBoolean("permissions_tips", true)
                                        .apply();

                            }
                        })
                .setNegativeButton(R.string.text_forbid,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sp.edit().putBoolean("permissions_allow", false)
                                        .putBoolean("permissions_tips", false)
                                        .apply();
                            }
                        })
                .setCancelable(false)
                .create();
        dialog.show();

    }
}