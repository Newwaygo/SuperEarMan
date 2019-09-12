package com.cn.superearman.modules.main.activity;

import android.app.Application;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatButton;

import com.cn.superearman.R;
import com.cn.superearman.app.App;
import com.cn.superearman.base.BaseMvpActivity;
import com.cn.superearman.constants.Config;
import com.cn.superearman.modules.main.entity.UserInfoEntity;
import com.cn.superearman.modules.main.manager.ManagerUserTrackViews;
import com.cn.superearman.widget.AdminTrackView;
import com.cn.superearman.widget.UserTrackView;
import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.callback.IZegoRoomCallback;
import com.zego.zegoliveroom.constants.ZegoConstants;
import com.zego.zegoliveroom.constants.ZegoVideoViewMode;
import com.zego.zegoliveroom.entity.ZegoStreamInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ZUserActivity extends BaseMvpActivity {


    @BindView(R.id.admin_track)
    AdminTrackView adminTrack;
    @BindView(R.id.qn_secondary_one)
    UserTrackView qnSecondaryOne;
    @BindView(R.id.qn_secondary_two)
    UserTrackView qnSecondaryTwo;
    @BindView(R.id.qn_secondary_three)
    UserTrackView qnSecondaryThree;
    @BindView(R.id.qn_secondary_four)
    UserTrackView qnSecondaryFour;
    @BindView(R.id.qn_secondary_five)
    UserTrackView qnSecondaryFive;
    @BindView(R.id.qn_secondary_six)
    UserTrackView qnSecondarySix;
    @BindView(R.id.qn_secondary_seven)
    UserTrackView qnSecondarySeven;
    @BindView(R.id.qn_secondary_eight)
    UserTrackView qnSecondaryEight;
    @BindView(R.id.btn_pick_up)
    AppCompatButton btnPickUp;
    @BindView(R.id.iv_pull_up)
    ImageView ivPullUp;
    @BindView(R.id.track_small)
    UserTrackView trackSmall;
    String userName, roomID;
    String userID;

    ZegoLiveRoom zegoLiveRoom;

    List<UserInfoEntity> userInfoArray;

    ManagerUserTrackViews managerUserTrackViews;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.pl_video)
    TextureView plVideo;
    @BindView(R.id.media_player)
    View  mediaPlayer;

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_room;
    }

    @Override
    public void initData() {
        super.initData();
        // 获取用户名，房间号
        roomID = getIntent().getStringExtra(Config.ROOM_NAME);
        userName = getIntent().getStringExtra(Config.ROOM_USER_NAME);

        // 初始化sdk ,
        userID = String.valueOf(System.currentTimeMillis());
        ZegoLiveRoom.setSDKContext(new ZegoLiveRoom.SDKContextEx() {
            @Override
            public long getLogFileSize() {
                return 500000000;
            }

            @Override
            public String getSubLogFolder() {
                return null;
            }

            @Override
            public String getSoFullPath() {
                return null;
            }

            @Override
            public String getLogPath() {
                return null;
            }

            @Override
            public Application getAppContext() {
                return App.getInstance();
            }
        });
        ZegoLiveRoom.setTestEnv(true);
        ZegoLiveRoom.setVerbose(true);
        zegoLiveRoom = new ZegoLiveRoom();
        zegoLiveRoom.setRoomConfig(true, true);

        zegoLiveRoom.setZegoRoomCallback(new IZegoRoomCallback() {
            @Override
            public void onKickOut(int i, String s) {
            }

            @Override
            public void onDisconnect(int i, String s) {

            }

            @Override
            public void onReconnect(int i, String s) {

            }

            @Override
            public void onTempBroken(int i, String s) {

            }

            @Override
            public void onStreamUpdated(int type, ZegoStreamInfo[] zegoStreamInfos, String s) {
                // 有新的拉流, 和退出的流
                for (ZegoStreamInfo zegoStreamInfo : zegoStreamInfos) {
                    switch (type) {
                        case ZegoConstants.StreamUpdateType.Added: // 分配房间
                            if ("isAdmin".equals(zegoStreamInfo.extraInfo)) {
                                zegoLiveRoom.startPlayingStream(zegoStreamInfo.streamID, adminTrack.getAdminTrackView());
                                // 设置拉流视图模式，此处采用 SDK 默认值--等比缩放填充整个 view，可能有部分被裁减。
                                zegoLiveRoom.setViewMode(ZegoVideoViewMode.ScaleAspectFill, zegoStreamInfo.streamID);
                            } else if ("video".equals(zegoStreamInfo.extraInfo)) {
                                zegoLiveRoom.startPlayingStream(zegoStreamInfo.streamID, plVideo);
                                // 设置拉流视图模式，此处采用 SDK 默认值--等比缩放填充整个 view，可能有部分被裁减。
                                zegoLiveRoom.setViewMode(ZegoVideoViewMode.ScaleAspectFill, zegoStreamInfo.streamID);
                            } else {
                                UserInfoEntity userRoom = managerUserTrackViews.onSubscribed(zegoStreamInfo);
                                zegoLiveRoom.startPlayingStream(zegoStreamInfo.streamID, userRoom.userTrackView.getUserTrackView());
                                // 设置拉流视图模式，此处采用 SDK 默认值--等比缩放填充整个 view，可能有部分被裁减。
                                zegoLiveRoom.setViewMode(ZegoVideoViewMode.ScaleAspectFill, zegoStreamInfo.streamID);
                            }
                            break;
                        case ZegoConstants.StreamUpdateType.Deleted: // 回收房间
                            managerUserTrackViews.onRemove(zegoStreamInfo.userName);
                            // 如果此条流是正在观看的流，停止拉取此条流
                            zegoLiveRoom.stopPlayingStream(zegoStreamInfo.streamID);
                            break;
                    }
                }
            }

            @Override
            public void onStreamExtraInfoUpdated(ZegoStreamInfo[] zegoStreamInfos, String s) {

            }

            @Override
            public void onRecvCustomCommand(String s, String s1, String s2, String s3) {

            }
        });
        zegoLiveRoom.initSDK(Config.appId, Config.appSign, errorCode -> {
            // errorCode 非0 代表初始化sdk失败
            // 具体错误码说明请查看<a> https://doc.zego.im/CN/308.html </a>
            if (errorCode != 0) {
                showMessage("初始化失败");
            } else {
                // sdk 初始化成功
                ZegoLiveRoom.setUser(userID, userName);
                zegoLiveRoom.loginRoom(roomID, userName, ZegoConstants.RoomRole.Anchor, (state, zegoStreamInfos) -> {
                    // 房间登录成功  开始推流
                    if (state == 0) {
                        startPublish(zegoStreamInfos);
                    }
                });
            }
        });
    }

    public void startPublish(ZegoStreamInfo[] zegoStreamInfos) {
        String streamID = String.valueOf(System.currentTimeMillis());
        ZegoStreamInfo current = new ZegoStreamInfo();
        current.userName = userName;
        current.streamID = streamID;
        current.extraInfo = "notAdmin";
        UserInfoEntity userInfoEntity = managerUserTrackViews.onSubscribed(current);
        zegoLiveRoom.setPreviewView(userInfoEntity.userTrackView.getUserTrackView());
        zegoLiveRoom.setPreviewViewMode(ZegoVideoViewMode.ScaleAspectFill);
        zegoLiveRoom.startPreview();
        zegoLiveRoom.startPublishing(streamID, "lili", ZegoConstants.PublishFlag.JoinPublish, "notAdmin");
        if (zegoStreamInfos.length > 0) {
            for (ZegoStreamInfo zegoStreamInfo : zegoStreamInfos) {
                if ("isAdmin".equals(zegoStreamInfo.extraInfo)) {
                    zegoLiveRoom.startPlayingStream(zegoStreamInfo.streamID, adminTrack.getAdminTrackView());
                    // 设置拉流视图模式，此处采用 SDK 默认值--等比缩放填充整个 view，可能有部分被裁减。
                    zegoLiveRoom.setViewMode(ZegoVideoViewMode.ScaleAspectFill, zegoStreamInfo.streamID);
                } else if ("video".equals(zegoStreamInfo.extraInfo)) {
                    zegoLiveRoom.startPlayingStream(zegoStreamInfo.streamID, plVideo);
                    // 设置拉流视图模式，此处采用 SDK 默认值--等比缩放填充整个 view，可能有部分被裁减。
                    zegoLiveRoom.setViewMode(ZegoVideoViewMode.ScaleAspectFill, zegoStreamInfo.streamID);


                } else {
                    UserInfoEntity userRoom = managerUserTrackViews.onSubscribed(zegoStreamInfo);
                    userRoom.stremID = zegoStreamInfo.streamID;
                    userRoom.userName = zegoStreamInfo.userName;
                    zegoLiveRoom.startPlayingStream(zegoStreamInfo.streamID, userRoom.userTrackView.getUserTrackView());
                    // 设置拉流视图模式，此处采用 SDK 默认值--等比缩放填充整个 view，可能有部分被裁减。
                    zegoLiveRoom.setViewMode(ZegoVideoViewMode.ScaleAspectFill, zegoStreamInfo.streamID);
                }
            }
        }

    }


    @Override
    public void initView() {
        userInfoArray = new ArrayList<>();
        userInfoArray.add(0, new UserInfoEntity(qnSecondaryOne));
        userInfoArray.add(1, new UserInfoEntity(qnSecondaryTwo));
        userInfoArray.add(2, new UserInfoEntity(qnSecondaryThree));
        userInfoArray.add(3, new UserInfoEntity(qnSecondaryFour));
        userInfoArray.add(4, new UserInfoEntity(qnSecondaryFive));
        userInfoArray.add(5, new UserInfoEntity(qnSecondarySix));
        userInfoArray.add(6, new UserInfoEntity(qnSecondarySeven));
        userInfoArray.add(7, new UserInfoEntity(qnSecondaryEight));
        managerUserTrackViews = new ManagerUserTrackViews(userInfoArray);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        zegoLiveRoom.stopPublishing();
        zegoLiveRoom.unInitSDK();
    }


    @OnClick({R.id.iv_pull_up, R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_pull_up:
                break;
            case R.id.iv_back:
                mediaPlayer.setVisibility(View.GONE);

                break;
        }
    }
}
