package com.cn.superearman.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.widget.FrameLayout;
import com.cn.superearman.R;

public class AdminTrackView extends FrameLayout {
    TextureView  adminTrackView;

    public AdminTrackView(Context context) {
        this(context,null);
    }

    public AdminTrackView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AdminTrackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    public void init(){
        LayoutInflater.from(getContext()).inflate(getLayout(), this, true);
    }
    private int getLayout() {
        return  R.layout.layout_admin_track;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        adminTrackView=findViewById(R.id.admin_screen);
    }
    public TextureView getAdminTrackView() {
        return adminTrackView;
    }
}
