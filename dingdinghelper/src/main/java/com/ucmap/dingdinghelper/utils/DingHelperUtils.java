package com.ucmap.dingdinghelper.utils;

import android.app.KeyguardManager;
import android.content.Context;
import android.util.Log;

import com.ucmap.dingdinghelper.entity.AccountEntity;
import com.ucmap.dingdinghelper.sphelper.SPUtils;
import com.ucmap.dingdinghelper.timing.TimingManagerUtil;

import java.util.Random;

/**
 * <b>@项目名：</b> Helmet<br>
 * <b>@包名：</b>com.ucmap.helmet<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> 宝诺科技<br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 */

public class DingHelperUtils {

    private static Random mRandom = new Random();

    public static void setAlarm(AccountEntity accountEntity, Context context) {

        String gTime = (String) SPUtils.getString(Constants.MORNING_CHECK_IN_TIME, "8:45");
        String[] hm = gTime.split(":");
        String nTime = (String) SPUtils.getString(Constants.AFTERNOON_CHECK_IN_TIME, "20:45");
        String[] nm = nTime.split(":");
        //截取手机号码后四位作为通知id
        int id = Integer.parseInt(accountEntity.getAccount().substring(accountEntity.getAccount().length() - 4, accountEntity.getAccount().length()));
        for (int i = 1; i <= 6; i++) {
//            int tm = isAdd() ? Integer.parseInt(hm[1]) + mRandom.nextInt(5) : Integer.parseInt(hm[1]) - mRandom.nextInt(5);
            TimingManagerUtil.setTiming(context, 2, Integer.parseInt(hm[0]), Integer.parseInt(hm[1]), id + i, i);
            Log.i("Infoss", "闹钟ID:" + (id + i) + "   hour:" + Integer.parseInt(hm[0]) + "   min: " + Integer.parseInt(hm[1]) + "   week:" + i);
//            int ta = isAdd() ? Integer.parseInt(nm[1]) + mRandom.nextInt(5) : Integer.parseInt(nm[1]) - mRandom.nextInt(5);
            TimingManagerUtil.setTiming(context, 2, Integer.parseInt(nm[0]), Integer.parseInt(nm[1]), id + i + 10, i);
            Log.i("Infoss", "闹钟ID:" + (id + i + 10) + "   hour:" + Integer.parseInt(nm[0]) + "   min: " + Integer.parseInt(nm[1]) + "   week:" + i);
        }
    }

    private static boolean isAdd() {
        return mRandom.nextBoolean();
    }

    /**
     * 系统是否在锁屏状态
     *
     * @return
     */
    public static boolean isScreenLocked(Context context) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return keyguardManager.inKeyguardRestrictedInputMode();
    }
}
