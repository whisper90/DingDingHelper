package com.ucmap.dingdinghelper.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.ucmap.dingdinghelper.ITimerListener;
import com.ucmap.dingdinghelper.ITimingAidlInterface;
import com.ucmap.dingdinghelper.R;
import com.ucmap.dingdinghelper.entity.AccountEntity;
import com.ucmap.dingdinghelper.sphelper.SPUtils;
import com.ucmap.dingdinghelper.ui.MainActivity;
import com.ucmap.dingdinghelper.utils.Constants;
import com.ucmap.dingdinghelper.utils.DateUtils;
import com.ucmap.dingdinghelper.utils.JsonUtils;

import java.util.List;

import static com.ucmap.dingdinghelper.utils.DateUtils.getHourAndMin;

/**
 * <b>@项目名：</b> Helmet<br>
 * <b>@包名：</b>com.ucmap.helmet<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> 宝诺科技<br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 */

public class TimingService extends Service {
    /**
     * 通知的ID
     */
    private static final int NOTIFICATION_ID = 0x0033888;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Infoss", "TimingService  oncreate");
        init();
        increasePriority();
    }

    private List<AccountEntity> mAccountEntities = null;
    private AccountEntity mAccountEntity = null;

    private int hour = 8;
    private int min = 45;

    private NotifyThread mNotifyThread;

    /*0表示早上打卡，1表示晚上打卡*/
    private static int STATE = 0;

    private void init() {
        String jsonAccountList = (String) SPUtils.getString(Constants.ACCOUNT_LIST, "-1");
        mAccountEntities = JsonUtils.listJson(jsonAccountList, AccountEntity.class);
        if (mAccountEntities == null || mAccountEntities.isEmpty()) {
            return;
        }
        this.mAccountEntity = mAccountEntities.get(0);

        /*try {
            *//* 启动守护进程，一分钟监测一次， 如果进程被kill 会重新拉起 *//*
            Daemon.run(this, TimingService.class, Daemon.INTERVAL_ONE_MINUTE);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        initCheckInTime(null);
        if (mNotifyThread == null || !mNotifyThread.isAlive()) {
            mNotifyThread = new NotifyThread();
            runing_monitor = true;
            mNotifyThread.setPriority(Thread.MAX_PRIORITY);
        /*启动监控线程*/
            mNotifyThread.start();
        }
    }

    private void initCheckInTime(String remoteTime) {
        String current = DateUtils.getHourAndMin(System.currentTimeMillis());
        int hour = Integer.parseInt(current.split(":")[0]);
        int min = Integer.parseInt(current.split(":")[1]);
        if (TextUtils.isEmpty(remoteTime)) {
            String time = "";
            if (hour < 12) {
                STATE = 0;
                time = (String) SPUtils.getString(Constants.MORNING_CHECK_IN_TIME, "8:45");
            } else {
                STATE = 1;
                time = (String) SPUtils.getString(Constants.AFTERNOON_CHECK_IN_TIME, "20:45");
            }


            if (time.length() >= 4) {
                String[] hourAndMin = time.split(":");
                this.hour = Integer.parseInt(hourAndMin[0]);
                this.min = Integer.parseInt(hourAndMin[1]);
                Log.i("Infoss", "time:" + this.hour + ":" + this.min);
            }
        } else {


            String hm[] = remoteTime.split(":");

            int tempH = Integer.parseInt(hm[0]);
            int tempM = Integer.parseInt(hm[1]);
            if (hour < 12 && ((tempH > hour) || (tempH == hour && tempM > min))) {

                STATE = 0;
                this.hour = tempH;
                this.min = tempM;

            } else if (hour >= 12 && ((tempH > hour) || (tempH == hour && tempM > min))) {

                STATE = 1;
                this.hour = tempH;
                this.min = tempM;
            } else {
                Log.i("Infoss", "  执行 为空");
                initCheckInTime(null);
            }
            String t = (String) SPUtils.getString(Constants.AFTERNOON_CHECK_IN_TIME, "8:45");

            Log.i("Infoss", "t:" + t);

        }

        Log.i("Infoss", "target Time:" + this.hour + ":" + this.min);
    }


    private RemoteCallbackList<ITimerListener> mITimerListenerRemoteCallbackList = new RemoteCallbackList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ITimingAidlInterface.Stub() {

            @Override
            public void registerTimerListener(ITimerListener timerListener) throws RemoteException {
                mITimerListenerRemoteCallbackList.register(timerListener);
            }

            @Override
            public void unRegisterTimerListener(ITimerListener timerListener) throws RemoteException {
                mITimerListenerRemoteCallbackList.unregister(timerListener);
            }

            @Override
            public void reInitCheckInTime(String time) throws RemoteException {
                initCheckInTime(time);
            }
        };
    }

    /*提升service 的优先级*/
    private void increasePriority() {
        Log.i("Infoss", "increasePriority:" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Notification.Builder mBuilder = new Notification.Builder(this);
            mBuilder.setSmallIcon(R.mipmap.app_logo);
            startForeground(NOTIFICATION_ID, mBuilder.build());
            InnerService.startInnerService(this);
        } else {
            startForeground(NOTIFICATION_ID, new Notification());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Infoss", "TimingService  onDestroy");
        runing_monitor = false;
        if (mNotifyThread != null && mNotifyThread.isAlive()) {

            Log.i("Infoss", " 杀死线程");
            /*如果Thread 正在休眠需要中断休眠 避免泄漏*/
            if (!mNotifyThread.isInterrupted()) {
                mNotifyThread.interrupt();
            }
        }
    }

    /*提升优先级,然而对AccessibilityService 并没有什么卵用*/
    public static class InnerService extends Service {
        private static final void startInnerService(Context context) {
            context.startService(new Intent(context, InnerService.class));
        }

        @Override
        public void onCreate() {
            super.onCreate();
            Log.i("Infoss", "a service oncreate");
            Notification.Builder mBuilder = new Notification.Builder(this);
            mBuilder.setSmallIcon(R.mipmap.app_logo);
            startForeground(NOTIFICATION_ID, mBuilder.build());


            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopForeground(true);
                    NotificationManager mNotificationManager = (NotificationManager) InnerService.this.getSystemService(NOTIFICATION_SERVICE);
                    mNotificationManager.cancel(NOTIFICATION_ID);
                    stopSelf();
                }
            }, 50);
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            stopForeground(true);
        }

    }

    private static final int NOTIFICATION_ID_PROGRESS_ID = 0x100100;

    private int[] parseTime(String time) {
        if (TextUtils.isEmpty(time))
            return null;
        String[] hm = time.split(":");
        int[] times = new int[hm.length];
        for (int i = 0; i < hm.length; i++) {
            times[i] = Integer.parseInt(hm[i]);
        }
        return times;
    }

    private void setTargetTimeForLockIn() {
        String time = "";
        time = (String) SPUtils.getString(Constants.MORNING_CHECK_IN_TIME, "6:45");
        int[] hm = parseTime(time);
        this.hour = hm[0];
        this.min = hm[1];
    }

    private void sendNotification(int hour, int min) {


        Log.i("Infoss", "当前状态:" + STATE + "  hour:" + hour + "  min:" + min);
        if ((STATE == 0) && (this.hour < hour || (this.hour == hour && this.min <= min))) {


            String time = "";
            time = (String) SPUtils.getString(Constants.AFTERNOON_CHECK_IN_TIME, "20:45");
            STATE = 1;

            String[] hm = time.split(":");
            this.hour = Integer.parseInt(hm[0]);
            this.min = Integer.parseInt(hm[1]);
            /*重进该方法*/
            sendNotification(hour, min);
            Log.i("Infoss", "时间:" + this.hour + "  :  " + this.min);
            return;
        } else if (STATE == 1 && (this.hour < hour || (this.hour == hour && this.min <= min))) {
            setTargetTimeForLockIn();
            STATE = 2;
            sendNotification(hour, min);
            Log.i("Infoss", "else if 时间:" + this.hour + "  :  " + this.min);
            return;
        } else if (STATE == 2) {
            if (hour < 23 && hour >= 12) {
                setTargetTimeForLockIn();
                Log.i("Infoss", "设置时间为:" + this.hour + "  min:" + this.min);
            } else
                STATE = 0;
        }
        int lHour = 0;

        if (hour <= this.hour) {
            lHour = this.hour - hour;
        } else {
            lHour = 24 - hour + this.hour;
        }

        int lMin = 0;
        if (min <= this.min) {
            lMin = this.min - min;
        } else {
            if (this.hour != hour)
                lHour--;
            if (hour == this.hour) {
                lMin = this.min - min;
            } else
                lMin = 60 - min + this.min;
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.app_logo);
        notifyUpdateUi("打卡:" + this.hour + ":" + this.min + ", 剩余" + lHour + "小时 " + lMin + " 分钟");
        Log.i("Infoss", "sendNotification 打卡时间是:" + this.hour + ":" + this.min + " 当前时间为: " + hour + ":" + min + "   " + "距离打卡时间还有: " + lHour + " 个小时" + lMin + " 分");
        mBuilder.setContentText("距离打卡时间还有: " + lHour + " 个小时" + lMin + " 分").setContentTitle("钉钉自动打卡");
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(mPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID_PROGRESS_ID, mBuilder.build());
    }

    /*用于监控线程生命周期控制*/
    private volatile boolean runing_monitor = true;

    private void notifyUpdateUi(String time) {
        if (mITimerListenerRemoteCallbackList == null)
            return;
        int number = mITimerListenerRemoteCallbackList.beginBroadcast();

        for (int i = 0; i < number; i++) {

            ITimerListener mITimerListener = mITimerListenerRemoteCallbackList.getBroadcastItem(i);
            if (mITimerListener == null)
                continue;
            try {
                mITimerListener.toCallback(time);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mITimerListenerRemoteCallbackList.finishBroadcast();
    }

    /*监控线程 */
    class NotifyThread extends Thread {
        @Override
        public void run() {

            /*把优先级级别提到最高,保证监控线程最优先运行*/
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
            Log.i("Infoss", "start NotifyThread:" + runing_monitor);
            while (runing_monitor) {


                String time = getHourAndMin(System.currentTimeMillis());
                Log.i("Infoss", "time:" + time + "       currentThreadTimeMillis:" + SystemClock.currentThreadTimeMillis() + "   currentTimeMillis:" + System.currentTimeMillis() + "  elapsedRealtime:" + SystemClock.elapsedRealtime() + "  uptimeMillis :" + SystemClock.uptimeMillis());
                if (TextUtils.isEmpty(time))
                    continue;
                String[] hourAndMin = time.split(":");
                if (hourAndMin == null || hourAndMin.length < 2)
                    continue;
                String hour = hourAndMin[0];
                int hourInt = Integer.parseInt(hour);
                int min = Integer.parseInt(hourAndMin[1]);
                sendNotification(hourInt, min);
                try {
                    /*休眠----每10sNotify一次*/
                    sleep(1000 * 10);
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                    Log.i("Infoss", "已经被中断:" + e.getLocalizedMessage());
                    return;
                }

            }
        }
    }
}
