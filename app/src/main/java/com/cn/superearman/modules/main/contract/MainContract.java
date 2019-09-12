package com.cn.superearman.modules.main.contract;

import com.cn.superearman.base.BaseView;

import io.reactivex.Flowable;

public interface MainContract {

    interface Model {
         Flowable<String> indexFunction(String roomName,String userId,String packageName);
    }
    interface View extends BaseView {
        void showRoomToken(String token);
    }
    interface Presenter {
        void indexFunction(String roomName,String userId,String packageName);
    }
}
