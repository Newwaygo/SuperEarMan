package com.cn.superearman.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.cn.superearman.R;
import com.cn.superearman.util.DensityUtil;


public class DefaultLoadingDialog extends Dialog {

    private TextView tvMessage;
    public DefaultLoadingDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_default_loading);
         tvMessage =findViewById(R.id.tv_message);
        Window window = getWindow();
        if(window != null) {
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = (int) (DensityUtil.getDisplayWidthPixels() * 0.3);
            layoutParams.height = (int) (DensityUtil.getDisplayWidthPixels() * 0.3);
            window.setAttributes(layoutParams);
        }
    }
    public void setMessage(String message){
        if(!TextUtils.isEmpty(message)){
            tvMessage.setText(message);
        }
    }
    @Override
    public void show() {
        super.show();
    }

}
