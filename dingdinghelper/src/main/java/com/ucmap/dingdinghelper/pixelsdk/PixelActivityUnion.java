package com.ucmap.dingdinghelper.pixelsdk;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * <b>@项目名：</b> Helmet<br>
 * <b>@包名：</b>com.ucmap.helmet<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> 宝诺科技<br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 */

public class PixelActivityUnion {


    private static final PixelActivityUnion mPixelActivityUnion = new PixelActivityUnion();
    private Class<? extends Activity> activity = null;
    private Bundle mBundle = null;
    private IActivityManager mActivityManager;

    private ScreenStateBroadcast mScreenStateBroadcast = null;
    private Context mContext;

    private static volatile int index = 0;

    private PixelActivityUnion() {
        if (mPixelActivityUnion != null) {
            throw new UnsupportedOperationException("single instance has been created,");
        }
    }

    public static PixelActivityUnion with(Context context) {
        mPixelActivityUnion.mContext = context.getApplicationContext();
        return mPixelActivityUnion;
    }

    public PixelActivityUnion targetActivityClazz(Class<? extends Activity> activity) {
        mPixelActivityUnion.activity = activity;
        return mPixelActivityUnion;
    }

    public PixelActivityUnion args(@Nullable Bundle bundle) {
        mPixelActivityUnion.mBundle = bundle;
        return mPixelActivityUnion;
    }


    public PixelActivityUnion setActiviyManager(IActivityManager manager) {
        this.mActivityManager = manager;
        return mPixelActivityUnion;
    }

    public static void start() {
        mPixelActivityUnion.doRegister();
    }

    public static void quit() {
        mPixelActivityUnion.exit();
    }

    public void exit() {
        if (mScreenStateBroadcast != null) {
            mContext.unregisterReceiver(mScreenStateBroadcast);
        }
        if (mActivityManager != null && activity != null) {
            mActivityManager.removeAcitivtyByClazz(activity);
        }
    }

    private void doRegister() {

        if (mContext == null)
            throw new NullPointerException("context is null");
        if (activity == null)
            throw new NullPointerException("target activity must nonnull");
        Log.i("Infoss", "已经注册广播");
        mScreenStateBroadcast = new ScreenStateBroadcast();
        IntentFilter mIntentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        mIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mContext.registerReceiver(mScreenStateBroadcast, mIntentFilter);
    }


    class ScreenStateBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i("Infoss", "action:" + intent.getAction());
            /*开锁*/
            if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                onScreenOn();
            } else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                onScrenntOff();
            }
        }
    }

    private void onScreenOn() {
        mActivityManager.removeAcitivtyByClazz(activity);
    }

    private void onScrenntOff() {
        Intent mIntent = new Intent(mContext, activity);
        if (mBundle != null)
            mIntent.putExtras(mBundle);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(mIntent);
    }

}
