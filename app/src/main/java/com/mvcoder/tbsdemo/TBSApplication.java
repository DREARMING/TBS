package com.mvcoder.tbsdemo;

import android.app.Application;
import android.os.Build;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.tencent.smtt.sdk.QbSdk;

public class TBSApplication extends Application {

    public static boolean hasX5Core = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StringBuilder builder = new StringBuilder();
            for(String abi : Build.SUPPORTED_ABIS){
                builder.append(abi);
            }
            LogUtils.e("abis : " + builder.toString());
        } else {
            LogUtils.e("abis : " + Build.CPU_ABI2);
        }

        initX5Core();
    }

    private void initX5Core() {

        QbSdk.PreInitCallback x5InitCb = new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {

            }

            @Override
            public void onViewInitFinished(boolean b) {
                LogUtils.d("X5内核初始化结果 ： " + b);
                hasX5Core = b;
            }
        };
        QbSdk.initX5Environment(this, x5InitCb);
    }
}
