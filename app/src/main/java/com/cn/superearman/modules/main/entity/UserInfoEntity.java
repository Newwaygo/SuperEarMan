package com.cn.superearman.modules.main.entity;

import com.cn.superearman.widget.UserTrackView;

public class UserInfoEntity {
     public String userName; // 用户名，
     public String stremID;  // 推流Id；
     public UserTrackView userTrackView; // 推流view .

    public UserInfoEntity(UserTrackView userTrackView) {
        this.userTrackView = userTrackView;
    }
}
