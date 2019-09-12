package com.cn.superearman.base;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import com.cn.superearman.widget.DefaultLoadingDialog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.AutoDisposeConverter;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

public abstract class BaseMvpActivity <T extends BasePresenter> extends BaseActivity implements BaseView {

    protected T mPresenter;

    DefaultLoadingDialog defaultLoadingDialog;

    public boolean isTouchHide=false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initData() {

    }

    public void showDefaultLoadingDialog(){
        if(defaultLoadingDialog==null){
            defaultLoadingDialog =new DefaultLoadingDialog(this);
        }
        defaultLoadingDialog.show();
    }
    public void showDefaultLoadingDialog(String message){
        if(defaultLoadingDialog==null){
            defaultLoadingDialog =new DefaultLoadingDialog(this);
        }
        defaultLoadingDialog.setMessage(message);
        defaultLoadingDialog.show();
    }
    public void dismissDefaultLoadingDialog(){
        if(defaultLoadingDialog!=null&&defaultLoadingDialog.isShowing()){
            defaultLoadingDialog.dismiss();
        }
    }
    @Override
    public void showMessage(String message) {
        showToast(message);
    }
    public void showToast(String message){
        if(!TextUtils.isEmpty(message)){
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 点击空白区域隐藏键盘.
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        if (isTouchHide) {
            if (me.getAction() == MotionEvent.ACTION_DOWN) {  //把操作放在用户点击的时候
                View v = getCurrentFocus();      //得到当前页面的焦点,ps:有输入框的页面焦点一般会被输入框占据
                if (isShouldHideKeyboard(v, me)) { //判断用户点击的是否是输入框以外的区域
                    hideKeyboard(v.getWindowToken());   //收起键盘
                }
            }
        }
        try {
            return super.dispatchTouchEvent(me);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    protected boolean touchHideKeyBoard(boolean isTouchHide) {
        return this.isTouchHide =isTouchHide;
    }
    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     */
    @SuppressWarnings("all")
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {  //判断得到的焦点控件是否包含EditText
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],    //得到输入框在屏幕中上下左右的位置
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            return !(event.getX() > left) || !(event.getX() < right)
                    || !(event.getY() > top) || !(event.getY() < bottom);
        }
        // 如果焦点不是EditText则忽略
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (im != null && getCurrentFocus() != null)
                im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    /**
     * 绑定生命周期 防止MVP内存泄漏
     *
     * @param <T>
     * @return
     */
    @Override
    public <T> AutoDisposeConverter<T> bindAutoDispose() {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider
                .from(this, Lifecycle.Event.ON_DESTROY));
    }


    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        super.onDestroy();
    }



}
