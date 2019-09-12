package com.cn.superearman.widget;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.widget.FrameLayout;
import com.cn.superearman.R;

public class UserTrackView extends FrameLayout {

    TextureView userTrackView;

    public UserTrackView(Context context) {
        this(context,null);
    }

    public UserTrackView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public UserTrackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    public void init(){
        LayoutInflater.from(getContext()).inflate(getLayout(), this, true);
    }
    private int getLayout() {
        return  R.layout.layout_user_track;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        userTrackView=findViewById(R.id.user_screen);
    }
    public TextureView getUserTrackView() {
        return userTrackView;
    }
}
