package com.cn.superearman.modules.main.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.cn.superearman.R;
import com.cn.superearman.constants.Config;
import com.cn.superearman.modules.main.activity.AdminActivity;
import com.cn.superearman.modules.main.activity.UserActivity;
import com.facebook.drawee.view.SimpleDraweeView;


public class AttendClassFragment extends Fragment {

    SimpleDraweeView ivCover;
    AppCompatTextView tvStep, tvJoinClass;
    String roomName;
    String userName;
    String token;
    String[] imagesUrl = {
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1566473979807&di=56860fc369a43efbe515e6f7e460ca57&imgtype=jpg&src=http%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20170803%2Fe7bf535b7af14361a746966008f45d07_th.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1566473909317&di=fff445894a787f0b137772cb25c48d9d&imgtype=0&src=http%3A%2F%2Fimg0.ph.126.net%2FvYixzIpZ3F9PEdpnwlr61w%3D%3D%2F6597252285587176220.jpg",
            "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=24676817,2006934320&fm=15&gp=0.jpg",
            "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1947354090,2958328652&fm=15&gp=0.jpg",
            "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1788039124,2164779571&fm=15&gp=0.jpg"
    };
    int position = 0;
    Activity mActivity;
    public static Fragment newInstance(int position, String roomName, String userName, String token) {
        AttendClassFragment fragment = new AttendClassFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putString(Config.ROOM_NAME, roomName);
        bundle.putString(Config.ROOM_USER_NAME, userName);
        bundle.putString(Config.ROOM_TOKEN, token);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity=(Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attend_class, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        ivCover = view.findViewById(R.id.iv_cover);
        tvStep = view.findViewById(R.id.tv_curriculum_step);
        tvJoinClass = view.findViewById(R.id.tv_join_class);
        tvJoinClass.setOnClickListener(view1 -> {
            if (getActivity() != null) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), view1);
                popupMenu.getMenu().add("老师");
                popupMenu.getMenu().add("学生");
                popupMenu.setOnMenuItemClickListener(item -> {
                    String itemTitle = item.getTitle().toString();
                    switch (itemTitle) {
                        case "老师":
                            adminRoom();
                            break;
                        case "学生":
                            userRoom();
                            break;
                    }
                    return false;
                });
                popupMenu.show();
            }
        });
    }

    private void userRoom() {
        Intent intent=new Intent();
        intent.setClass(mActivity, UserActivity.class);
        intent.putExtra(Config.ROOM_NAME,roomName);
        intent.putExtra(Config.ROOM_USER_NAME,userName);
        intent.putExtra(Config.ROOM_TOKEN,token);
        startActivity(intent);
    }

    private void adminRoom() {
         Intent intent=new Intent();
         intent.setClass(mActivity,AdminActivity.class);
         intent.putExtra(Config.ROOM_NAME,roomName);
         intent.putExtra(Config.ROOM_USER_NAME,userName);
         intent.putExtra(Config.ROOM_TOKEN,token);
         startActivity(intent);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            roomName = bundle.getString(Config.ROOM_NAME);
            userName = bundle.getString(Config.ROOM_USER_NAME);
            token = bundle.getString(Config.ROOM_TOKEN);
            position = bundle.getInt("position");
        }
        tvStep.setText(String.format("第%S课", position + 1));
        if (position < imagesUrl.length) {
            ivCover.setImageURI(imagesUrl[position]);
        }
    }
}
