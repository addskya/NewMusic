
package com.lz.music.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lz.music.kuyuehui.R;

public class VipFragment extends Fragment {

    @Override
    @SuppressLint("InflateParams")
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_vip, container, false);
    }

    public static VipFragment newInstance() {
        return new VipFragment();
    }
}