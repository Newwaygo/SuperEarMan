package com.cn.superearman.modules.main.activity;

import android.content.Intent;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.viewpager.widget.ViewPager;

import com.cn.superearman.R;
import com.cn.superearman.base.BaseMvpActivity;
import com.cn.superearman.constants.Config;
import com.cn.superearman.modules.main.adapter.AttendClassAdapter;

import butterknife.BindView;

public class CurriculumActivity extends BaseMvpActivity {


    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    AttendClassAdapter attendClassAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_curriculum;
    }

    @Override
    public void initView() {
        Intent intent = getIntent();
        String roomName=intent.getStringExtra(Config.ROOM_NAME);
        String userName=intent.getStringExtra(Config.ROOM_USER_NAME);
        String token =intent.getStringExtra(Config.ROOM_TOKEN);
        attendClassAdapter=new AttendClassAdapter(getSupportFragmentManager(),roomName,userName,token);
        viewpager.setAdapter(attendClassAdapter);
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



}
