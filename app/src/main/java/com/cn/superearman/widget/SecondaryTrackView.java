package com.cn.superearman.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatTextView;

import com.cn.superearman.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qiniu.droid.rtc.QNRTCEngine;
import com.qiniu.droid.rtc.QNSurfaceView;
import com.qiniu.droid.rtc.QNTrackInfo;
import com.qiniu.droid.rtc.QNTrackKind;

import java.util.List;

/**
 *  自定义控件
 * */
public class SecondaryTrackView extends FrameLayout {
    QNSurfaceView qnStudent ;
    ImageView ivMute;
    SimpleDraweeView ivHead;
    AppCompatTextView tvUserName;
    public SecondaryTrackView(Context context) {
        this(context,null);
    }
    public SecondaryTrackView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public SecondaryTrackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public void init(){
        LayoutInflater.from(getContext()).inflate(getLayout(), this, true);
    }
    private int getLayout() {
        return  R.layout.layout_secondary_screen_track;
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        qnStudent =findViewById(R.id.qn_student);
        ivMute=findViewById(R.id.iv_mute);
        ivHead=findViewById(R.id.iv_head);
        tvUserName=findViewById(R.id.tv_user_name);
    }

    public void onSubscribed(QNRTCEngine mQNRTCEngine, String name, List<QNTrackInfo> trackInfoList){
        ivHead.setVisibility(View.GONE);
        qnStudent.setVisibility(View.VISIBLE);
        tvUserName.setVisibility(View.VISIBLE);
        tvUserName.setText(name);
        for (QNTrackInfo qnTrackInfo : trackInfoList) {
            QNTrackKind kind = qnTrackInfo.getTrackKind();
            switch (kind) {
                case AUDIO:
                    break;
                case VIDEO:
                    mQNRTCEngine.setRenderWindow(qnTrackInfo, qnStudent);
                    break;
            }
        }
    }
    public void onRemoteUnpublished(){
        ivHead.setVisibility(View.VISIBLE);
        qnStudent.setVisibility(View.GONE);
        qnStudent.release();
        tvUserName.setText("");
    }
    public void setQnStudentView(boolean isShow){
        qnStudent.setVisibility(isShow?View.VISIBLE:View.GONE);
    }
    public void setOverLay(boolean overLay){
        qnStudent.setZOrderMediaOverlay(overLay);
        qnStudent.setZOrderOnTop(overLay);
    }


}
