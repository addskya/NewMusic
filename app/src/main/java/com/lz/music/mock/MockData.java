
package com.lz.music.mock;

import java.util.ArrayList;
import java.util.List;

import com.cmsc.cmmusic.common.data.ChartInfo;
import com.cmsc.cmmusic.common.data.MusicInfo;

public class MockData {
    public static List<ChartInfo> getChartList() {
        List<ChartInfo> list = new ArrayList<ChartInfo>();
        for (int i = 0; i < 8; i++) {
            ChartInfo chart = new ChartInfo();
            chart.setChartCode("code_" + i);
            chart.setChartName("ChartName_" + i);
            list.add(chart);
        }

        return list;
    }

    public static List<MusicInfo> getMusicList() {
        List<MusicInfo> list = new ArrayList<MusicInfo>();
        for (int i = 0; i < 30; i++) {
            MusicInfo music = new MusicInfo();
            music.setMusicId("id_" + i);
            music.setSongName("Song_" + i);
            music.setSingerName("Singer_" + i);
            list.add(music);
        }

        return list;
    }
}