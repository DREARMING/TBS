package com.mvcoder.tbsdemo;

import android.app.Application;
import android.os.Build;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.tencent.smtt.sdk.QbSdk;

public class TBSApplication extends Application {

    public static boolean hasX5Core = false;
    private long time = System.currentTimeMillis();

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
        //QbSdk.do(true);
        initX5Core();
    }

    private void initX5Core() {

        QbSdk.PreInitCallback x5InitCb = new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {

            }

            @Override
            public void onViewInitFinished(boolean b) {
                //确实会自动下载内核，但是花费时间比较长，最少也要10秒，在这段时间内需要控制用户对内核的访问。
                //第一次成功下载后，重启app x5内核才生效,就是说进程必须kill一次才生效.
                //而用第三方的内核，初始化成功后就可以马上使用。
                LogUtils.d("X5内核初始化结果 ： " + b);
                long wasteTime = System.currentTimeMillis() - time;
                LogUtils.d("x5 init waste time : " + wasteTime);
                hasX5Core = b;
            }
        };

        QbSdk.initX5Environment(this, x5InitCb);
    }
}
