package com.cn.superearman.modules.main.presenter;

import com.cn.superearman.base.BasePresenter;
import com.cn.superearman.modules.main.contract.MainContract;
import com.cn.superearman.modules.main.model.MainModel;
import com.cn.superearman.net.ExceptionHandle;
import com.cn.superearman.net.RxScheduler;

@SuppressWarnings("ALL")
public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {

    MainContract.Model model;
    public MainPresenter() {
        model =new MainModel();
    }

    @Override
    public void indexFunction(String roomName, String userId, String packageName) {
        model.indexFunction(roomName,userId,packageName)
                .compose(RxScheduler.Flo_io_main())
                .subscribe(s -> {
                    mView.showRoomToken(s);
                }, throwable -> {
                    mView.showMessage(ExceptionHandle.handleException(throwable).message);
                });
    }
}
