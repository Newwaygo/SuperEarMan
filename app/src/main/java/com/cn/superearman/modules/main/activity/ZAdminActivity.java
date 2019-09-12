package com.cn.superearman.modules.main.activity;

import android.app.Application;
import android.graphics.Bitmap;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatTextView;

import com.cn.superearman.R;
import com.cn.superearman.app.App;
import com.cn.superearman.base.BaseMvpActivity;
import com.cn.superearman.constants.Config;
import com.cn.superearman.modules.main.entity.UserInfoEntity;
import com.cn.superearman.modules.main.manager.ManagerUserTrackViews;
import com.cn.superearman.modules.main.media.ZGVideoCaptureForMediaPlayer;
import com.cn.superearman.widget.AdminTrackView;
import com.cn.superearman.widget.UserTrackView;
import com.zego.zegoavkit2.IZegoMediaPlayerCallback;
import com.zego.zegoavkit2.IZegoMediaPlayerVideoPlayCallback;
import com.zego.zegoavkit2.ZegoExternalVideoCapture;
import com.zego.zegoavkit2.ZegoMediaPlayer;
import com.zego.zegoavkit2.ZegoVideoCaptureFactory;
import com.zego.zegoavkit2.ZegoVideoDataFormat;
import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.callback.IZegoLivePublisherCallback;
import com.zego.zegoliveroom.callback.IZegoRoomCallback;
import com.zego.zegoliveroom.constants.ZegoConstants;
import com.zego.zegoliveroom.constants.ZegoVideoViewMode;
import com.zego.zegoliveroom.entity.AuxData;
import com.zego.zegoliveroom.entity.ZegoPublishStreamQuality;
import com.zego.zegoliveroom.entity.ZegoStreamInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ZAdminActivity extends BaseMvpActivity implements IZegoLivePublisherCallback {

    String userName, roomID;

    String userID;

    ZegoLiveRoom zegoLiveRoom;
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
    @BindView(R.id.tv_pack_up)
    AppCompatTextView tvPackUp;

    List<UserInfoEntity> userInfoArray;

    ManagerUserTrackViews managerUserTrackViews;
    @BindView(R.id.tv_enlightenment)
    AppCompatTextView tvEnlightenment;
    @BindView(R.id.tv_interpretation)
    AppCompatTextView tvInterpretation;
    @BindView(R.id.tv_classroom)
    AppCompatTextView tvClassroom;
    @BindView(R.id.tv_task)
    AppCompatTextView tvTask;
    @BindView(R.id.tv_mute)
    AppCompatTextView tvMute;
    @BindView(R.id.tv_whiteboard)
    AppCompatTextView tvWhiteboard;
    @BindView(R.id.tv_see_each_other)
    AppCompatTextView tvSeeEachOther;
    @BindView(R.id.tv_pack_up_menu)
    AppCompatTextView tvPackUpMenu;
    @BindView(R.id.iv_end_pull_up_secondary)
    ImageView ivEndPullUpSecondary;

    @BindView(R.id.pl_video)
    TextureView plVideo;

    @BindView(R.id.media_player)
     View mediaPlayer;

    ZegoMediaPlayer zegoMediaPlayer;
    private String videoPath = "http://vfx.mtime.cn/Video/2019/03/18/mp4/190318214226685784.mp4";
//    String videoPath="https://vfx.mtime.cn/Video/2019/01/15/mp4/190115161611510728_480.mp4";

    // 播放视频外部采集类，用于实现Zego SDK外部采集
    private ZGVideoCaptureForMediaPlayer.ZGMediaPlayerVideoCapture zgMediaPlayerVideoCapture = null;

    private ZGVideoCaptureForMediaPlayer zgVideoCaptureForMediaPlayer;


    String streamIDByVideo;

    public   interface playVideData{
        void onPlayVideoData(byte[] bytes, int size, ZegoVideoDataFormat f);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_admin_room;
    }

    @Override
    public void initData() {
        super.initData();


        // 获取用户名，房间号
        roomID = getIntent().getStringExtra(Config.ROOM_NAME);
        userName = getIntent().getStringExtra(Config.ROOM_USER_NAME);

        // 初始化sdk ,
        userID = String.valueOf(System.currentTimeMillis());
        streamIDByVideo=String.valueOf(System.currentTimeMillis());
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
                            UserInfoEntity userRoom = managerUserTrackViews.onSubscribed(zegoStreamInfo);
                            zegoLiveRoom.startPlayingStream(zegoStreamInfo.streamID, userRoom.userTrackView.getUserTrackView());
                            // 设置拉流视图模式，此处采用 SDK 默认值--等比缩放填充整个 view，可能有部分被裁减。
                            zegoLiveRoom.setViewMode(ZegoVideoViewMode.ScaleAspectFill, zegoStreamInfo.streamID);
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
        zgMediaPlayerVideoCapture =new ZGVideoCaptureForMediaPlayer.ZGMediaPlayerVideoCapture(zegoLiveRoom);
        zgVideoCaptureForMediaPlayer=new ZGVideoCaptureForMediaPlayer(zgMediaPlayerVideoCapture);
        ZegoExternalVideoCapture.setVideoCaptureFactory(zgVideoCaptureForMediaPlayer, ZegoConstants.PublishChannelIndex.AUX);
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
        zegoLiveRoom.setZegoLivePublisherCallback(this);
        zegoMediaPlayer = new ZegoMediaPlayer();
        initMediaPlayer();
    }

    private void initMediaPlayer() {
        // 初始化播放器
        zegoMediaPlayer.init(ZegoMediaPlayer.PlayerTypeAux);
        zegoMediaPlayer.setView(plVideo);
        // 播放器状态回调。
        zegoMediaPlayer.setCallback(new IZegoMediaPlayerCallback() {
            @Override
            public void onPlayStart() {

            }

            @Override
            public void onPlayPause() {

            }

            @Override
            public void onPlayStop() {

            }

            @Override
            public void onPlayResume() {

            }

            @Override
            public void onPlayError(int i) {

            }

            @Override
            public void onVideoBegin() {

            }

            @Override
            public void onAudioBegin() {

            }

            @Override
            public void onPlayEnd() {

            }

            @Override
            public void onBufferBegin() {

            }

            @Override
            public void onBufferEnd() {

            }

            @Override
            public void onSeekComplete(int i, long l) {

            }

            @Override
            public void onSnapshot(Bitmap bitmap) {

            }

            @Override
            public void onLoadComplete() {

            }

            @Override
            public void onProcessInterval(long l) {

            }
        });
        zegoMediaPlayer.setVideoPlayCallback((bytes, i, zegoVideoDataFormat) -> zgMediaPlayerVideoCapture.onPlayVideoData(bytes,i,zegoVideoDataFormat),ZegoVideoDataFormat .PIXEL_FORMAT_RGBA32);

        zegoLiveRoom.startPublishing2(streamIDByVideo,"视频",ZegoConstants.PublishFlag.MixStream,"video", ZegoConstants.PublishChannelIndex.AUX);


    }

    public void startPublish(ZegoStreamInfo[] zegoStreamInfos) {
        String streamID = String.valueOf(System.currentTimeMillis());
        zegoLiveRoom.setPreviewView(adminTrack.getAdminTrackView());
        zegoLiveRoom.setPreviewViewMode(ZegoVideoViewMode.ScaleAspectFill);
        zegoLiveRoom.startPreview();
        zegoLiveRoom.startPublishing(streamID, "lili", ZegoConstants.PublishFlag.JoinPublish, "isAdmin");
//        zegoLiveRoom.startPublishing2(streamID, "lili", ZegoConstants.PublishFlag.JoinPublish, "isAdmin",12);
        if (zegoStreamInfos.length > 0) {
            for (ZegoStreamInfo zegoStreamInfo : zegoStreamInfos) {
                zegoLiveRoom.startPlayingStream(zegoStreamInfo.streamID, qnSecondaryOne.getUserTrackView());
                // 设置拉流视图模式，此处采用 SDK 默认值--等比缩放填充整个 view，可能有部分被裁减。
                zegoLiveRoom.setViewMode(ZegoVideoViewMode.ScaleAspectFill, zegoStreamInfo.streamID);
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
        zegoLiveRoom.unInitSDK();
        zegoMediaPlayer.stop();
    }
    @OnClick({R.id.tv_enlightenment, R.id.tv_interpretation, R.id.tv_classroom, R.id.tv_task, R.id.tv_mute, R.id.tv_whiteboard, R.id.tv_see_each_other, R.id.tv_pack_up_menu, R.id.iv_end_pull_up_secondary,R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_enlightenment: // 启蒙视频
                if(!mediaPlayer.isShown()){
                    mediaPlayer.setVisibility(View.VISIBLE);
                    zegoMediaPlayer.start(videoPath,true);
                    zegoMediaPlayer.resume();

                }else{
                    mediaPlayer.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_interpretation:
                break;
            case R.id.tv_classroom:
                break;
            case R.id.tv_task:
                break;
            case R.id.tv_mute:
                break;
            case R.id.tv_whiteboard:
                break;
            case R.id.tv_see_each_other:
                break;
            case R.id.tv_pack_up_menu:
                break;
            case R.id.iv_end_pull_up_secondary:
                break;
            case R.id.iv_back:
                zegoMediaPlayer.pause();
                mediaPlayer.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        zegoMediaPlayer.pause();
    }

    @Override
    public void onPublishStateUpdate(int i, String s, HashMap<String, Object> hashMap) {

    }

    @Override
    public void onJoinLiveRequest(int i, String s, String s1, String s2) {

    }

    @Override
    public void onPublishQualityUpdate(String s, ZegoPublishStreamQuality zegoPublishStreamQuality) {

    }

    @Override
    public AuxData onAuxCallback(int i) {
        return null;
    }

    @Override
    public void onCaptureVideoSizeChangedTo(int i, int i1) {

    }

    @Override
    public void onMixStreamConfigUpdate(int i, String s, HashMap<String, Object> hashMap) {

    }

    @Override
    public void onCaptureVideoFirstFrame() {

    }

    @Override
    public void onCaptureAudioFirstFrame() {

    }

}
