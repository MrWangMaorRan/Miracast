package com.zizhong.mobilescreen.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtils {
    private static Toast mToast;

    /**
     * 自定义内容
     */
    public static void showToast(Context context, String text) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (StringUtils.isNullOrBlank(text) || context == null)
                        return;
                    if (mToast != null) {
                        mToast.setText(text);
                    } else {
                        mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                    }
                    mToast.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 网络异常
     */
    public static void showNetError(Context context) {
        showToast(context, "网络异常，请稍候重试！");
    }

    /**
     * 无网络
     */
    public static void showNotInternet(Context context) {
        showToast(context, "无网络，请稍候重试！");
    }

}
