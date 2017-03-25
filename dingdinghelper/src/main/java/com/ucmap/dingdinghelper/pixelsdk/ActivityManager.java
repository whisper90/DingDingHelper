package com.ucmap.dingdinghelper.pixelsdk;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <b>@项目名：</b> Helmet<br>
 * <b>@包名：</b>com.ucmap.helmet<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> 宝诺科技<br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 */

public class ActivityManager implements IActivityManager {

    private static final List<WeakReference<Activity>> mActivities = new ArrayList<>();


    private static final AtomicReference<ActivityManager> atomic = new AtomicReference<>();

    private ActivityManager() {

    }

    public static ActivityManager getInstance() {

        for (; ; ) {
            ActivityManager mActivityManager = atomic.get();
            if (mActivityManager != null)
                return mActivityManager;
            if (atomic.compareAndSet(null, new ActivityManager())) {
                return atomic.get();
            }

        }
    }

    private boolean findTargetAndRemove(Class<? extends Activity> activityClazz) {
        boolean tag = false;
        //倒序remove
        for (int i = mActivities.size() - 1; i >= 0; i--) {

            WeakReference<Activity> mActivityWeakReference = mActivities.get(i);
            if (mActivityWeakReference.get() == null) {
                mActivities.remove(i);
            }
        }

        for (int i = 0; i < mActivities.size(); i++) {
            WeakReference<Activity> mWeakReference = mActivities.get(i);
            if (mWeakReference.get() != null && mWeakReference.get().getClass() == activityClazz) {
                mWeakReference.get().finish();
                mActivities.remove(i);
                tag = true;
                break;
            }
        }

        return tag;
    }

    public void addActivity(Activity activity) {
        if (activity != null) {
            findTargetAndRemove(activity.getClass());
            mActivities.add(new WeakReference<Activity>(activity));
        }
    }

    public void removeActivity(Activity activity) {
        findTargetAndRemove(activity.getClass());
    }


    @Override
    public void removeAcitivtyByClazz(Class<? extends Activity> clazz) {
        findTargetAndRemove(clazz);
    }
}
