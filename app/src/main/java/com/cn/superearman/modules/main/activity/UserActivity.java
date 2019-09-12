package com.cn.superearman.modules.main.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatButton;

import com.cn.superearman.R;
import com.cn.superearman.base.BaseMvpActivity;
import com.cn.superearman.constants.Config;
import com.cn.superearman.modules.main.entity.MainTrackEntity;
import com.cn.superearman.modules.main.entity.SecondaryTrackEntity;
import com.cn.superearman.modules.main.manager.ManagerMainTrackView;
import com.cn.superearman.modules.main.manager.ManagerSecondaryTrackView;
import com.cn.superearman.util.DensityUtil;
import com.cn.superearman.widget.MainScreenTrackView;
import com.cn.superearman.widget.SecondaryTrackView;
import com.gyf.immersionbar.ImmersionBar;
import com.qiniu.droid.rtc.QNCustomMessage;
import com.qiniu.droid.rtc.QNRTCEngine;
import com.qiniu.droid.rtc.QNRTCEngineEventListener;
import com.qiniu.droid.rtc.QNRTCSetting;
import com.qiniu.droid.rtc.QNRoomState;
import com.qiniu.droid.rtc.QNSourceType;
import com.qiniu.droid.rtc.QNStatisticsReport;
import com.qiniu.droid.rtc.QNTrackInfo;
import com.qiniu.droid.rtc.QNTrackKind;
import com.qiniu.droid.rtc.QNVideoFormat;
import com.qiniu.droid.rtc.model.QNAudioDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class UserActivity extends BaseMvpActivity implements QNRTCEngineEventListener {

    String roomName, userName, token;
    @BindView(R.id.qn_main_screen)
    MainScreenTrackView qnMainScreen;
    @BindView(R.id.qn_secondary_one)
    SecondaryTrackView qnSecondaryOne;
    @BindView(R.id.qn_secondary_two)
    SecondaryTrackView qnSecondaryTwo;
    @BindView(R.id.qn_secondary_three)
    SecondaryTrackView qnSecondaryThree;
    @BindView(R.id.qn_secondary_four)
    SecondaryTrackView qnSecondaryFour;
    @BindView(R.id.qn_secondary_five)
    SecondaryTrackView qnSecondaryFive;
    @BindView(R.id.qn_secondary_six)
    SecondaryTrackView qnSecondarySix;
    @BindView(R.id.qn_secondary_seven)
    SecondaryTrackView qnSecondarySeven;
    @BindView(R.id.qn_secondary_eight)
    SecondaryTrackView qnSecondaryEight;
    @BindView(R.id.btn_pick_up)
    AppCompatButton btnPickUp;
    @BindView(R.id.layout_user_secondary)
    View layoutUsers;
    @BindView(R.id.iv_pull_up)
    ImageView ivPullUp;
    @BindView(R.id.track_small)
    SecondaryTrackView smallTrack;
    @BindView(R.id.qn_main_small_screen)
    MainScreenTrackView mainSmallScreen;

    private QNRTCEngine mEngine;
    private int mCaptureMode = Config.CAMERA_CAPTURE;
    private static final String[] MANDATORY_PERMISSIONS = {
            "android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.RECORD_AUDIO",
            "android.permission.INTERNET"
    };
    private QNTrackInfo mLocalVideoTrack; // 视屏 track;
    private QNTrackInfo mLocalAudioTrack; // 音频 track;
    private QNTrackInfo mLocalScreenTrack;// 共享屏幕 track;
    private static final int BITRATE_FOR_SCREEN_VIDEO = (int) (1.5 * 1000 * 1000);
    private boolean mIsJoinedRoom = false;
    private List<QNTrackInfo> mLocalTrackList;
    int mScreenWidth, mScreenHeight;
    private ManagerSecondaryTrackView managerSecondaryTrackView;
    Map<String, List<QNTrackInfo>> map; // 记录用户播放QNTrackInfo.
    int index = -1;// 自己占用的小窗口,房间下标。
    ManagerMainTrackView managerMainTrackView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_user;
    }

    @Override
    protected void initImmersionBar() {
        immersionBar = ImmersionBar.with(this)
                .statusBarDarkFont(false)
                .keyboardEnable(true);
        immersionBar.init();
    }

    @Override
    public void initView() {
        mScreenWidth = DensityUtil.getDisplayWidthPixels();
        mScreenHeight = DensityUtil.getDisplayHeightPixels();
        for (String permission : MANDATORY_PERMISSIONS) {
            if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEngine.startCapture();
        if (!mIsJoinedRoom) {
            mEngine.joinRoom(token);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEngine.stopCapture();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEngine != null) {
            mEngine.destroy();
            mEngine = null;
        }
    }

    @Override
    public void initData() {
        super.initData();
        Intent intent = getIntent();
        roomName = intent.getStringExtra(Config.ROOM_NAME);
        userName = intent.getStringExtra(Config.ROOM_USER_NAME);
        token = intent.getStringExtra(Config.ROOM_TOKEN);
        initEngine();
        initManagerMainTrack();
        initSecondaryTrackView();
        initLocalTrackInfoList();


    }

    private void initManagerMainTrack() {
        Map<String, MainTrackEntity> map = new HashMap<>();
        MainTrackEntity mainTrackEntity = new MainTrackEntity(true, qnMainScreen);
        MainTrackEntity smallTrackEntity = new MainTrackEntity(false, mainSmallScreen);
        map.put("big", mainTrackEntity);
        map.put("small", smallTrackEntity);
        mainSmallScreen.getQnMainScreen().setZOrderMediaOverlay(true);
        mainSmallScreen.getQnMainScreen().setZOrderOnTop(true);
        managerMainTrackView = new ManagerMainTrackView(mEngine, map);
    }

    private void initLocalTrackInfoList() {
        mLocalTrackList = new ArrayList<>();
        mLocalAudioTrack = mEngine.createTrackInfoBuilder()
                .setSourceType(QNSourceType.AUDIO)
                .setMaster(true)
                .create();
        mLocalTrackList.add(mLocalAudioTrack);
        QNVideoFormat screenEncodeFormat = new QNVideoFormat(mScreenWidth / 2, mScreenHeight / 2, 20);
        switch (mCaptureMode) {
            case Config.CAMERA_CAPTURE:
                mLocalVideoTrack = mEngine.createTrackInfoBuilder()
                        .setSourceType(QNSourceType.VIDEO_CAMERA)
                        .setMaster(true)
                        .setTag(Config.TAG_CAMERA).create();
                mLocalTrackList.add(mLocalVideoTrack);
                break;
            case Config.ONLY_AUDIO_CAPTURE: // 没有配置。 应该是只有声音的意思
                break;
            case Config.SCREEN_CAPTURE: // 只有屏幕录制。
                mLocalScreenTrack = mEngine.createTrackInfoBuilder()
                        .setVideoPreviewFormat(screenEncodeFormat)
                        .setBitrate(BITRATE_FOR_SCREEN_VIDEO)
                        .setSourceType(QNSourceType.VIDEO_SCREEN)
                        .setMaster(true)
                        .setTag(Config.TAG_SCREEN).create();
                mLocalTrackList.add(mLocalScreenTrack);
                break;
            case Config.MUTI_TRACK_CAPTURE:
                mLocalScreenTrack = mEngine.createTrackInfoBuilder()
                        .setSourceType(QNSourceType.VIDEO_SCREEN)
                        .setVideoPreviewFormat(screenEncodeFormat)
                        .setBitrate(BITRATE_FOR_SCREEN_VIDEO)
                        .setMaster(true)
                        .setTag(Config.TAG_SCREEN).create();
                mLocalVideoTrack = mEngine.createTrackInfoBuilder()
                        .setSourceType(QNSourceType.VIDEO_CAMERA)
                        .setTag(Config.TAG_CAMERA).create();
                mLocalTrackList.add(mLocalScreenTrack);
                mLocalTrackList.add(mLocalVideoTrack);
                break;
        }
    }

    private void initSecondaryTrackView() {
        // 创建8人房间
        List<SecondaryTrackEntity> list = new ArrayList<>();
        list.add(new SecondaryTrackEntity(qnSecondaryOne));
        list.add(new SecondaryTrackEntity(qnSecondaryTwo));
        list.add(new SecondaryTrackEntity(qnSecondaryThree));
        list.add(new SecondaryTrackEntity(qnSecondaryFour));
        list.add(new SecondaryTrackEntity(qnSecondaryFive));
        list.add(new SecondaryTrackEntity(qnSecondarySix));
        list.add(new SecondaryTrackEntity(qnSecondarySeven));
        list.add(new SecondaryTrackEntity(qnSecondaryEight));// 初始化8个房间
        managerSecondaryTrackView = new ManagerSecondaryTrackView(mEngine, list);

    }

    private void initEngine() {
        boolean isHwCodec = false;
        boolean isMaintainRes = false;
        int videoBitrate = 700 * 1000;
        int videoWidth = 700;
        int fps = 20;
        QNVideoFormat format = new QNVideoFormat(720, 1280, fps);
        QNRTCSetting setting = new QNRTCSetting();
        setting.setCameraID(QNRTCSetting.CAMERA_FACING_ID.FRONT)
                .setHWCodecEnabled(isHwCodec)
                .setMaintainResolution(isMaintainRes)
                .setVideoBitrate(videoBitrate)
                .setVideoEncodeFormat(format)
                .setVideoPreviewFormat(format);
        mEngine = QNRTCEngine.createEngine(getApplicationContext(), setting, this);
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
    public void onRoomStateChanged(QNRoomState qnRoomState) {
        switch (qnRoomState) {
            case RECONNECTING:
                showMessage("重连中...");
                break;
            case RECONNECTED:
                showMessage("连接成功");
                break;
            case CONNECTED:
                mEngine.publishTracks(mLocalTrackList);
                mIsJoinedRoom = true;
                showMessage("连接成功");
                break;
            case CONNECTING:
                showMessage("连接中...");
                break;
        }
    }

    @Override
    public void onRemoteUserJoined(String remoteUserId, String s1) {

    }

    @Override
    public void onRemoteUserLeft(String remoteUserId) {

    }

    @Override
    public void onLocalPublished(List<QNTrackInfo> trackInfoList) {
        // 要获取房间
        index = managerSecondaryTrackView.onSubscribed(userName, trackInfoList);
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(userName, trackInfoList);
    }

    @Override
    public void onRemotePublished(String remoteUserId, List<QNTrackInfo> list) {
        showMessage("onRemotePublished");
    }

    @Override
    public void onRemoteUnpublished(String remoteUserId, List<QNTrackInfo> list) {
        // 普通的远端用户退出房间。
        if (!remoteUserId.equals("edr_admin")) {
            managerSecondaryTrackView.onRemoteUnpublished(remoteUserId, list);
        } else { // 检查到老师退出视频直播间。
            qnMainScreen.getQnMainScreen().release();
        }
    }

    @Override
    public void onRemoteUserMuted(String remoteUserId, List<QNTrackInfo> list) {

    }

    @Override
    public void onSubscribed(String remoteUserId, List<QNTrackInfo> list) {
        // 订阅到远端用户 是 admin  分配最大的房间，可还行？
        showMessage(remoteUserId);
        if (remoteUserId.equals("edr_admin")) {   // 房主进入房间。
            managerMainTrackView.onSubscribed(remoteUserId,list);
        } else {
            managerSecondaryTrackView.onSubscribed(remoteUserId, list);
        }
    }

    @Override
    public void onKickedOut(String s) {

    }

    @Override
    public void onStatisticsUpdated(QNStatisticsReport qnStatisticsReport) {

    }

    @Override
    public void onAudioRouteChanged(QNAudioDevice qnAudioDevice) {

    }

    @Override
    public void onCreateMergeJobSuccess(String remoteUserId) {

    }

    @Override
    public void onError(int i, String s) {

    }

    @Override
    public void onMessageReceived(QNCustomMessage qnCustomMessage) {

    }

    @OnClick({R.id.btn_pick_up, R.id.iv_pull_up})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_pick_up:// 简单的显示隐藏，后期可以加上动画
                layoutUsers.setVisibility(View.GONE);
                ivPullUp.setVisibility(View.VISIBLE);
                smallTrack.setVisibility(View.VISIBLE);
                smallTrack.setOverLay(true);
                List<QNTrackInfo> list = map.get(userName);
                smallTrack.onSubscribed(mEngine, userName, list);
                qnMainScreen.getQnMainScreen().setZOrderOnTop(false);
                qnMainScreen.getQnMainScreen().setZOrderMediaOverlay(false);
                managerSecondaryTrackView.setTrackListVisible(false);
                break;
            case R.id.iv_pull_up:
                layoutUsers.setVisibility(View.VISIBLE);
                ivPullUp.setVisibility(View.GONE);
                smallTrack.setVisibility(View.GONE);
                smallTrack.setOverLay(false);
                smallTrack.onRemoteUnpublished();
                list = map.get(userName);
                managerSecondaryTrackView.swap(userName, list, index);
                managerSecondaryTrackView.setTrackListVisible(true);
                break;
        }
    }
}
