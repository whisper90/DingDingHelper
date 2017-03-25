package com.ucmap.dingdinghelper.timing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.ucmap.dingdinghelper.app.App;
import com.ucmap.dingdinghelper.entity.MessageEvent;
import com.ucmap.dingdinghelper.services.DingDingHelperAccessibilityService;
import com.ucmap.dingdinghelper.utils.Constants;
import com.ucmap.dingdinghelper.utils.DingHelperUtils;
import com.ucmap.dingdinghelper.utils.ShellUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class TimingBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("Infoss", "TimingBroadcastReceiver  已经被系统回调");
        // TODO Auto-generated method stub
        long intervalMillis = intent.getLongExtra("intervalMillis", 0);
        if (intervalMillis != 0) {
            TimingManagerUtil.resetTiming(context, System.currentTimeMillis() + intervalMillis,
                    intent);
        }

        List<String> mList = new ArrayList<String>();
        if (!DingDingHelperAccessibilityService.IS_ENABLE_DINGDINGHELPERACCESSIBILITYSERVICE) {

            mList.add(Constants.POINT_SERVICES_ORDER);
            mList.add(Constants.ENABLE_SERVICE_PUT);

        }else{
        /*唤醒屏幕*/
            if (DingHelperUtils.isScreenLocked(App.mContext)) {
                mList.add("input keyevent 26");
                /*从下往上滑动解锁*/
                mList.add("input swipe 200 800 200 100");
            }

        }
        if(!mList.isEmpty()){
            ShellUtils.CommandResult mCommandResult = ShellUtils.execCmd(mList, true);
            Log.i("Infoss", "result:" + mCommandResult.toString());
        }


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new MessageEvent());
            }
        }, 1000);//
    }
}
