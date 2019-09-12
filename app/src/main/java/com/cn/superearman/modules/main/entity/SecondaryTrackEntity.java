package com.cn.superearman.modules.main.entity;

import com.cn.superearman.widget.SecondaryTrackView;
import com.qiniu.droid.rtc.QNTrackInfo;
import java.io.Serializable;
import java.util.List;



public class SecondaryTrackEntity implements Serializable {
    private static final long serialVersionUID = 1400564525434508999L;

    public  int id;
    public  String userId;
    public List<QNTrackInfo> trackInfo;
    public SecondaryTrackView trackView;
    public String name;
    public SecondaryTrackEntity() {
    }

    public SecondaryTrackEntity(SecondaryTrackView trackView) {
        this.trackView = trackView;
    }



}
