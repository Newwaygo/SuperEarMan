package com.cn.superearman.modules.main.manager;

import com.cn.superearman.modules.main.entity.MainTrackEntity;
import com.qiniu.droid.rtc.QNRTCEngine;
import com.qiniu.droid.rtc.QNSurfaceView;
import com.qiniu.droid.rtc.QNTrackInfo;
import com.qiniu.droid.rtc.QNTrackKind;

import java.util.List;
import java.util.Map;

/**
 * 主屏幕管理类
 */
public class ManagerMainTrackView {
    private QNRTCEngine mQNRTCEngine;
    private Map<String, MainTrackEntity> map;

    public ManagerMainTrackView(QNRTCEngine mQNRTCEngine, Map<String, MainTrackEntity> map) {
        this.mQNRTCEngine = mQNRTCEngine;
        this.map = map;
    }

    public void onSubscribed(String remoteUserId, List<QNTrackInfo> trackInfoList) {
        boolean isScreen = false;
        for (QNTrackInfo qnTrackInfo : trackInfoList) {
            QNTrackKind kind = qnTrackInfo.getTrackKind();
            switch (kind) {
                case AUDIO:
                    break;
                case VIDEO:
                    if ("screen".equals(qnTrackInfo.getTag())) {
                        isScreen = true;
                        QNSurfaceView mainTrack = map.get("big").mainTrackView.getQnMainScreen();
//                        mainTrack.setZOrderMediaOverlay(false);
//                        mainTrack.setZOrderOnTop(false);
                        mQNRTCEngine.setRenderWindow(qnTrackInfo, mainTrack);
                    }
                    if ("camera".equals(qnTrackInfo.getTag())) {
                            QNSurfaceView smallTrack = map.get("small").mainTrackView.getQnMainScreen();
                            mQNRTCEngine.setRenderWindow(qnTrackInfo, map.get("small").mainTrackView.getQnMainScreen());
                    }
                    break;
            }
        }
    }


}
