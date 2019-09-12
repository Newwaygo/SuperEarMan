package com.cn.superearman.modules.main.model;

import com.cn.superearman.modules.main.contract.MainContract;
import com.cn.superearman.net.RetrofitClient;

import io.reactivex.Flowable;

public class MainModel implements MainContract.Model {

    @Override
    public Flowable<String> indexFunction(String roomName,String userId,String packageName) {
        return RetrofitClient.getInstance().getApi().indexFunction(roomName,userId,packageName);
    }
}
