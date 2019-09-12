package com.cn.superearman.modules.main.manager;

import android.text.TextUtils;
import com.cn.superearman.modules.main.entity.SecondaryTrackEntity;
import com.qiniu.droid.rtc.QNRTCEngine;
import com.qiniu.droid.rtc.QNTrackInfo;

import java.util.List;


public class ManagerSecondaryTrackView {

    private QNRTCEngine mQNRTCEngine;
    private List<SecondaryTrackEntity> secondaryTrackList;

    public ManagerSecondaryTrackView(QNRTCEngine mQNRTCEngine, List<SecondaryTrackEntity> secondaryTrackList) {
        this.mQNRTCEngine = mQNRTCEngine;
        this.secondaryTrackList = secondaryTrackList;
    }

    /**
     * 远端用户进入,分配房间
     */
    public int onSubscribed(String remoteUserId, List<QNTrackInfo> trackInfoList) {
        // 循环遍历获取未分配房间
        SecondaryTrackEntity useTrack = getUnUseTrack();
        int index=secondaryTrackList.indexOf(useTrack);
        if (useTrack != null) {
            useTrack.userId = remoteUserId;
            useTrack.trackInfo = trackInfoList;
            useTrack.trackView.onSubscribed(mQNRTCEngine, remoteUserId, trackInfoList);
            return index;
        }
        return index;
    }
    /**
     * 远端用户取消，屏幕推送
     */
    public boolean onRemoteUnpublished(String remoteUserId, List<QNTrackInfo> trackInfoList) {
        SecondaryTrackEntity useTrack = getUseTrack(remoteUserId);
        if(useTrack!=null){
            useTrack.userId="";
            useTrack.trackView.onRemoteUnpublished();
            return true;
        }
        return  false;
    }
    public SecondaryTrackEntity getUseTrack(String remoteUserId) {
        SecondaryTrackEntity useTrack = null;
        for (SecondaryTrackEntity secondaryTrackEntity : secondaryTrackList) {
            if (remoteUserId.equals(secondaryTrackEntity.userId)) {
                useTrack = secondaryTrackEntity;
                break;
            }
        }
        return useTrack;
    }

    public SecondaryTrackEntity getUnUseTrack() {
        SecondaryTrackEntity unUseTrack = null;
        for (SecondaryTrackEntity secondaryTrackEntity : secondaryTrackList) {
            if (TextUtils.isEmpty(secondaryTrackEntity.userId)) {
                unUseTrack = secondaryTrackEntity;
                break;
            }
        }
        return unUseTrack;
    }
    // 要回自己的房间
    public void swap(String remoteUserId, List<QNTrackInfo> trackInfoList ,int index){
        if(index>=0){
            SecondaryTrackEntity  selfTrack = secondaryTrackList.get(index);
            selfTrack.trackView.onSubscribed(mQNRTCEngine,remoteUserId,trackInfoList);
        }
    }
    //
    public void setTrackListVisible(boolean isShow){
        for (SecondaryTrackEntity secondaryTrackEntity : secondaryTrackList) {
            secondaryTrackEntity.trackView.setQnStudentView(isShow);
        }
    }

}
