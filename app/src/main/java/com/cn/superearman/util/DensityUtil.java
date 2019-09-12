package com.cn.superearman.util;


import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.cn.superearman.app.App;


/**
 * DensityUtil
 * 屏幕密度尺寸相关工具类
 * Created by Tomato.wl on 2018/10/30.
 */

public class DensityUtil {

    private DensityUtil() {

    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static float dip2px(float dpValue) {
        final float scale = App.getInstance().getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5f;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static float px2dip(float pxValue) {
        final float scale = App.getInstance().getResources().getDisplayMetrics().density;
        return pxValue / scale + 0.5f;
    }

    /**
     * 获取屏幕宽度
     * @return 屏幕宽度
     */
    public static int getDisplayWidthPixels() {
        Resources resources = App.getInstance().getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    /**
     * 获取屏幕高度
     * @return 屏幕高度
     */
    public static int getDisplayHeightPixels() {
        Resources resources = App.getInstance().getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        return displayMetrics.heightPixels;
    }
}
