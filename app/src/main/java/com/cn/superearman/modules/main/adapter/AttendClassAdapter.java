package com.cn.superearman.modules.main.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.cn.superearman.modules.main.fragment.AttendClassFragment;

public class AttendClassAdapter extends  SmartFragmentStatePagerAdapter {

    String roomName,userName,token;
    public AttendClassAdapter(FragmentManager fragmentManager,String roomName,String userName,String token) {
        super(fragmentManager);
        this.roomName=roomName;
        this.userName=userName;
        this.token=token;

    }

    @Override
    public Fragment getItem(int position) {
        return AttendClassFragment.newInstance(position,roomName,userName,token);
    }

    @Override
    public int getCount() {
        return 5;
    }
}
