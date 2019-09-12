package com.cn.superearman.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.cn.superearman.R;
import com.qiniu.droid.rtc.QNSurfaceView;

public class MainScreenTrackView extends FrameLayout {
    QNSurfaceView qnMainScreen;

    public MainScreenTrackView(Context context) {
        this(context,null);
    }

    public MainScreenTrackView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MainScreenTrackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    public void init(){
        LayoutInflater.from(getContext()).inflate(getLayout(), this, true);
    }
    private int getLayout() {
        return  R.layout.layout_main_screen;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        qnMainScreen=findViewById(R.id.qn_main_surface);
    }
    public QNSurfaceView getQnMainScreen(){
        return qnMainScreen;
    }
}
