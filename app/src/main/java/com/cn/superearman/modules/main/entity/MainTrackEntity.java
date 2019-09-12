package com.cn.superearman.modules.main.entity;

import com.cn.superearman.widget.MainScreenTrackView;

import java.io.Serializable;

public class MainTrackEntity  implements Serializable {
    private static final long serialVersionUID = -3972778609801216919L;
    public boolean isBig;// 大窗 标志
    public MainScreenTrackView mainTrackView;

    public MainTrackEntity(boolean isBig, MainScreenTrackView mainTrackView) {
        this.isBig = isBig;
        this.mainTrackView = mainTrackView;
    }
}
