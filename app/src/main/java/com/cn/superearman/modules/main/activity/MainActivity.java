package com.cn.superearman.modules.main.activity;

import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.widget.CheckBox;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import com.cn.superearman.R;
import com.cn.superearman.base.BaseEntity;
import com.cn.superearman.base.BaseMvpActivity;
import com.cn.superearman.constants.Config;
import com.cn.superearman.modules.main.contract.MainContract;
import com.cn.superearman.modules.main.presenter.MainPresenter;
import com.cn.superearman.util.PermissionChecker;
import com.google.gson.Gson;
import com.qiniu.droid.rtc.QNScreenCaptureUtil;
import com.zego.zegoliveroom.constants.ZegoConstants;

import java.util.Objects;
import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseMvpActivity<MainPresenter> implements MainContract.View {
    @BindView(R.id.et_user_name)
    AppCompatEditText etName;
    @BindView(R.id.bt_start)
    AppCompatButton btStart;
    @BindView(R.id.et_room_name)
    AppCompatEditText etRoomName;
    @BindView(R.id.cb_role)
    CheckBox cbRole;
    String userName;
    String roomName;
    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }
    @Override
    public void initView() {
        isTouchHide = true;
        isPermissionOK();
        cbRole.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) { // 房主调用录屏权限。
//                QNScreenCaptureUtil.requestScreenCapture(this);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QNScreenCaptureUtil.SCREEN_CAPTURE_PERMISSION_REQUEST_CODE &&
                QNScreenCaptureUtil.onActivityResult(requestCode, resultCode, data)) {
             //  获取录屏权限成功。
        }


    }

    private boolean isPermissionOK() {
        PermissionChecker checker = new PermissionChecker(this);
        boolean isPermissionOK = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checker.checkPermission();
        if (!isPermissionOK) {
            showMessage("Some permissions is not approved !!!");
        }
        return isPermissionOK;
    }

    @Override
    public void initData() {
        mPresenter = new MainPresenter();
        mPresenter.attachView(this);
    }

    @OnClick(R.id.bt_start)
    public void onViewClicked() {
        if (checkInfo()) {
            String permission = "user";
            boolean isAnchor=false;
            if (cbRole.isChecked()) {
                permission = "admin";
                isAnchor=true;
            }
//            QNAppServer appServer = QNAppServer.getInstance();
//            new Thread(() -> {
//               String token= appServer.requestRoomToken(MainActivity.this,userName,roomName);
//               runOnUiThread(() -> {
//                   if (!TextUtils.isEmpty(token)) { // 房间号，拿到了，传给下个界面。
//                       Intent intent =new Intent();
//                       intent.putExtra(Config.ROOM_NAME,roomName);
//                       intent.putExtra(Config.ROOM_USER_NAME,userName);
//                       intent.putExtra(Config.ROOM_TOKEN,token);
//                       intent.setClass(MainActivity.this,CurriculumActivity.class);
//                       startActivity(intent);
//                   }else{
//                       showMessage("未获得房间令牌");
//                   }
//               });
//            }).start();
//            mPresenter.indexFunction(roomName, userName, permission);
           if(isAnchor){
               Intent intent =new Intent();
               intent.setClass(MainActivity.this,ZAdminActivity.class);
               intent.putExtra(Config.ROOM_NAME, roomName);
               intent.putExtra(Config.ROOM_USER_NAME, userName);
               startActivity(intent);
           }else {
               Intent intent =new Intent();
               intent.setClass(MainActivity.this,ZUserActivity.class);
               intent.putExtra(Config.ROOM_NAME, roomName);
               intent.putExtra(Config.ROOM_USER_NAME, userName);
               startActivity(intent);
           }
        }
    }

    private boolean checkInfo() {
        userName = Objects.requireNonNull(etName.getText()).toString().trim();
        roomName = Objects.requireNonNull(etRoomName.getText()).toString().trim();
        if (TextUtils.isEmpty(userName)) {
            showMessage("请输入昵称!");
            return false;
        }
        if (TextUtils.isEmpty(roomName)) {
            showMessage("请输入房间号!");
            return false;
        }
        return true;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {
    }

    @Override
    public void showMessage(String message) {
        super.showMessage(message);
    }

    @Override
    public void onError(Throwable throwable) {
    }

    @Override
    public void showRoomToken(String token) {
        BaseEntity tokenEntity = new Gson().fromJson(token, BaseEntity.class);
        if (!TextUtils.isEmpty(token)) { // 房间号，拿到了，传给下个界面。
            Intent intent = new Intent();
            intent.putExtra(Config.ROOM_NAME, roomName);
            intent.putExtra(Config.ROOM_USER_NAME, userName);
            intent.putExtra(Config.ROOM_TOKEN, tokenEntity.data);
            intent.setClass(this, CurriculumActivity.class);
            startActivity(intent);
        } else {
            showMessage("未获得房间令牌");
        }
    }

}
