package com.cn.superearman.modules.main.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatTextView;

import com.cn.superearman.R;
import com.cn.superearman.base.BaseMvpActivity;
import com.cn.superearman.constants.Config;
import com.cn.superearman.modules.main.entity.SecondaryTrackEntity;
import com.cn.superearman.modules.main.manager.ManagerSecondaryTrackView;
import com.cn.superearman.util.DensityUtil;
import com.cn.superearman.widget.MainScreenTrackView;
import com.cn.superearman.widget.SecondaryTrackView;
import com.gyf.immersionbar.ImmersionBar;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.qiniu.droid.rtc.QNAudioFormat;
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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

public class AdminActivity extends BaseMvpActivity implements QNRTCEngineEventListener {


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
    @BindView(R.id.tv_pack_up)
    AppCompatTextView tvPackUp;
    @BindView(R.id.layout_secondary_screen)
    View layoutSecondary;
    @BindView(R.id.layout_admin_menu)
    View layoutMenu;
    @BindView(R.id.iv_end_pull_up_secondary)
    ImageView ivEndPullUp;
    @BindView(R.id.layout_niu_player)
    View mNiuPlayer;
    @BindView(R.id.pl_video)
    PLVideoTextureView mPLVideoTextureView;
    @BindView(R.id.iv_back)
    ImageView ivBack;


    String roomName, userName, token;
    int mScreenWidth, mScreenHeight;
    private static final String[] MANDATORY_PERMISSIONS = {
            "android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.RECORD_AUDIO",
            "android.permission.INTERNET"
    };
    private QNRTCEngine mEngine;
    private int mCaptureMode = Config.MUTI_TRACK_CAPTURE;
    private List<QNTrackInfo> mLocalTrackList;
    private QNTrackInfo mLocalVideoTrack; // 视屏 track;
    private QNTrackInfo mLocalAudioTrack; // 音频 track;
    private QNTrackInfo mLocalScreenTrack;// 共享屏幕 track;
    private static final int BITRATE_FOR_SCREEN_VIDEO = (int) (1.5 * 1000 * 1000);
    private boolean mIsJoinedRoom = false;
    ManagerSecondaryTrackView managerSecondaryTrackView;
    private String videoPath = "http://demo-videos.qnsdk.com/movies/apple.mp4";
    QNRTCSetting setting;
    private QNVideoFormat screenEncodeFormat;

    @Override
    public int getLayoutId() {
        return R.layout.activity_admin;
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
    protected void initImmersionBar() {
        immersionBar = ImmersionBar.with(this)
                .statusBarDarkFont(false)
                .keyboardEnable(true);
        immersionBar.init();
    }

    @Override
    public void initData() {
        super.initData();
        Intent intent = getIntent();
        roomName = intent.getStringExtra(Config.ROOM_NAME);
        userName = intent.getStringExtra(Config.ROOM_USER_NAME);
        token = intent.getStringExtra(Config.ROOM_TOKEN);

        initEngine();
        initLocalTrackInfoList();
        initSecondaryTrackView();
        initVideoView();
    }

    private void initVideoView() {
        mPLVideoTextureView.setAVOptions(createAVOptions());
        mPLVideoTextureView.setDisplayAspectRatio(PLVideoTextureView.ASPECT_RATIO_PAVED_PARENT);
        mPLVideoTextureView.setOnVideoFrameListener((data, size, width, height, format, ts) -> {
            // TODO: resize video resolution ( with % 16 <> 0 )
            //Log.d(TAG, "onVideoFrameAvailable, format: " + format + ", width: " + width + ", height: " + height);
//                if (mLocalScreenTrack == null || TextUtils.isEmpty(mLocalScreenTrack.getTrackId())) {
//                    return;
//                }
//
//                if (format != 0) {
//                    if (!mVideoFormatErrorShowedUp) {
//                        mVideoFormatErrorShowedUp = true;
//                        runOnUiThread(() -> {
//                            updateRemoteLogText("仅支持\"NV12、宽度为16倍数\"的视频；当前视频数据格式错误，format：" + format + ", width: " + width + ", height: " + height);
//                        });
//                    }
//                    return;
//                }
//                if (mEngine != null) {
//                    byte[] temp = new byte[data.length];
//                    swapYV12toNV12(data, temp, width, height);
//                    QNVideoFrame videoFrame = new QNVideoFrame();
//                    videoFrame.buffer = temp;
//                    videoFrame.width = width;
//                    videoFrame.height = height;
//                    videoFrame.rotation = 0;
//                    videoFrame.timestampNs = ts * 1000000;
//                    mEngine.pushVideoBuffer(mLocalScreenTrack.getTrackId(), videoFrame);
//                }
        });
        mPLVideoTextureView.setOnAudioFrameListener((data, size, sampleRate, channels, dataWidth, ts) -> {
            Log.d("mPLVideoTextureView", "onAudioFrameAvailable, dataSize: " + data.length + " , size: " + size + ", sampleRate: " + sampleRate + ", channels: " + channels + ", dataWidth: " + dataWidth);
            if (mLocalAudioTrack == null) {
                return;
            }
            ByteBuffer buffer = ByteBuffer.allocate(size);
            buffer.put(data, 0, size);
            QNAudioFormat qnAudioFormat = new QNAudioFormat();
            qnAudioFormat.setChannels(channels);
            qnAudioFormat.setSampleRate(sampleRate);
            mEngine.pushAudioBuffer(buffer.array(), qnAudioFormat);
        });

        mPLVideoTextureView.setOnCompletionListener(() -> {
            // auto loop.
            mPLVideoTextureView.start();
        });
        mPLVideoTextureView.setVideoPath(videoPath);
    }

    private AVOptions createAVOptions() {
        AVOptions options = new AVOptions();
        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, 0);
        // 1 -> hw codec enable, 0 -> disable [recommended]
        options.setInteger(AVOptions.KEY_MEDIACODEC, AVOptions.MEDIA_CODEC_AUTO);
        options.setInteger(AVOptions.KEY_PREFER_FORMAT, AVOptions.PREFER_FORMAT_MP4);
        // options.setInteger(AVOptions.KEY_AUDIO_RENDER_EXTERNAL, 1);
        options.setInteger(AVOptions.KEY_VIDEO_RENDER_EXTERNAL, 1);
        options.setInteger(AVOptions.KEY_AUDIO_DATA_CALLBACK, 1);
        boolean disableLog = true;
        options.setInteger(AVOptions.KEY_LOG_LEVEL, disableLog ? 5 : 0);
        return options;
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

    private void initEngine() {
        boolean isHwCodec = false;
        boolean isMaintainRes = false;
        int videoBitrate = 600 * 1000;
        int videoWidth = 700;
        int fps = 15;
        QNVideoFormat format = new QNVideoFormat(1280, 720, fps);
         setting = new QNRTCSetting();
        setting.setCameraID(QNRTCSetting.CAMERA_FACING_ID.FRONT)
                .setHWCodecEnabled(isHwCodec)
                .setMaintainResolution(isMaintainRes)
                .setVideoBitrate(videoBitrate)
                .setVideoEncodeFormat(format)
                .setVideoPreviewFormat(format);
        mEngine = QNRTCEngine.createEngine(getApplicationContext(), setting, this);

    }

    private void initLocalTrackInfoList() {
        mLocalTrackList = new ArrayList<>();
        mLocalAudioTrack = mEngine.createTrackInfoBuilder()
                .setSourceType(QNSourceType.AUDIO)
                .setMaster(true)
                .create();
        mLocalTrackList.add(mLocalAudioTrack);
        screenEncodeFormat = new QNVideoFormat(mScreenWidth / 2, mScreenHeight / 2, 20);
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
                mLocalTrackList.add(mLocalScreenTrack);
                mLocalVideoTrack = mEngine.createTrackInfoBuilder()
                        .setSourceType(QNSourceType.VIDEO_CAMERA)
                        .setTag(Config.TAG_CAMERA).create();
                mLocalTrackList.add(mLocalVideoTrack);
                break;
        }
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

    @OnClick({R.id.tv_enlightenment, R.id.tv_interpretation, R.id.tv_classroom, R.id.tv_task, R.id.tv_mute, R.id.tv_whiteboard, R.id.tv_see_each_other, R.id.tv_pack_up, R.id.tv_pack_up_menu, R.id.iv_end_pull_up_secondary,R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_enlightenment: // 启蒙视频
                mNiuPlayer.setVisibility(View.VISIBLE);
                mPLVideoTextureView.start();
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
            case R.id.tv_pack_up:
                layoutSecondary.setVisibility(View.GONE);
                ivEndPullUp.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_pack_up_menu:
                layoutMenu.setVisibility(View.GONE);
                ivEndPullUp.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_end_pull_up_secondary:
                if (!layoutMenu.isShown()) {
                    layoutMenu.setVisibility(View.VISIBLE);
                }
                if (!layoutSecondary.isShown()) {
                    layoutSecondary.setVisibility(View.VISIBLE);
                }
                ivEndPullUp.setVisibility(View.GONE);
                break;
            case R.id.iv_back:
                mNiuPlayer.setVisibility(View.GONE);
                mPLVideoTextureView.pause();
                break;
        }
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
    public void onRemoteUserJoined(String s, String s1) {

    }

    @Override
    public void onRemoteUserLeft(String s) {

    }

    @Override
    public void onLocalPublished(List<QNTrackInfo> list) {
        for (QNTrackInfo qnTrackInfo : list) {
            QNTrackKind kind = qnTrackInfo.getTrackKind();
            switch (kind) {
                case AUDIO:
                    break;
                case VIDEO:
                    if (qnMainScreen.getQnMainScreen() != null) {
                        if(!"screen".equals(qnTrackInfo.getTag())){
                            mEngine.setRenderWindow(qnTrackInfo, qnMainScreen.getQnMainScreen());
                        }
                    } else {
                        showMessage("没有可以渲染的容器");
                    }
                    break;
            }
        }
        mEngine.enableStatistics();
    }

    @Override
    public void onRemotePublished(String s, List<QNTrackInfo> list) {

    }

    @Override
    public void onRemoteUnpublished(String remoteUserId, List<QNTrackInfo> list) {
        // 远端用户取消发布
        managerSecondaryTrackView.onRemoteUnpublished(remoteUserId, list);

    }

    @Override
    public void onRemoteUserMuted(String s, List<QNTrackInfo> list) {

    }

    @Override
    public void onSubscribed(String remoteUserId, List<QNTrackInfo> list) {
        // 远端用户进入房间
        int index = managerSecondaryTrackView.onSubscribed(remoteUserId, list);
        if (index < 0) {
            showMessage("无可用房间");
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
    public void onCreateMergeJobSuccess(String s) {

    }

    @Override
    public void onError(int i, String s) {

    }

    @Override
    public void onMessageReceived(QNCustomMessage qnCustomMessage) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEngine != null) {
            mEngine.destroy();
            mEngine = null;
        }
        if (mPLVideoTextureView != null) {
            mPLVideoTextureView.stopPlayback();
        }
    }

    @Override
    public void onBackPressed() {
        if(mNiuPlayer.isShown()){
            mPLVideoTextureView.pause();
            mNiuPlayer.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
        }
    }
}
