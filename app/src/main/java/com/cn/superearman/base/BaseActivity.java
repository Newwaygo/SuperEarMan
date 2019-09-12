package com.cn.superearman.base;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cn.superearman.R;
import com.gyf.immersionbar.ImmersionBar;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {

    private Unbinder unbinder;
    public ImmersionBar immersionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.getLayoutId());
        unbinder = ButterKnife.bind(this);
        initView();
        initData();
        initImmersionBar();
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
    protected void initImmersionBar() {
        immersionBar = ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.blue1296db)
                .statusBarDarkFont(true);
        immersionBar.init();
    }
    /**
     * 设置布局
     *
     * @return
     */
    public abstract int getLayoutId();

    /**
     * 初始化视图
     */
    public abstract void initView();

    /**
     *  初始化数据
     * */
    public abstract  void initData();



}
