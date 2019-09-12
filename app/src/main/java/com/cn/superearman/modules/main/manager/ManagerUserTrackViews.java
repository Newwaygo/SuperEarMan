package com.cn.superearman.modules.main.manager;


import android.text.TextUtils;
import com.cn.superearman.modules.main.entity.UserInfoEntity;
import com.zego.zegoliveroom.entity.ZegoStreamInfo;

import java.util.List;

/**
 * 用户 trackview  管理类，分配房间，回收房间。
 */
public class ManagerUserTrackViews {
    List<UserInfoEntity> userInfoList;

    public ManagerUserTrackViews(List<UserInfoEntity> userInfoList) {
        this.userInfoList = userInfoList;
    }

    // 分配一个空房间
    public UserInfoEntity onSubscribed(ZegoStreamInfo zegoStreamInfo) {
        UserInfoEntity userInfoEntity = null;
        for (int i = 0; i < userInfoList.size(); i++) {
            userInfoEntity = userInfoList.get(i);
            if (TextUtils.isEmpty(userInfoEntity.userName)) {
                userInfoEntity.stremID =zegoStreamInfo.streamID;
                userInfoEntity.userName=zegoStreamInfo.userName;
                break;
            }
        }
        return userInfoEntity;
    }

    // 回收房间
    public void onRemove(String userName){
        for (int i = 0; i < userInfoList.size(); i++) {
            UserInfoEntity userInfoEntity = userInfoList.get(i);
            if (userName.equals(userInfoEntity.userName)) {
                userInfoEntity.userName="";
                userInfoEntity.stremID="";
                break;
            }
        }

    }








}
