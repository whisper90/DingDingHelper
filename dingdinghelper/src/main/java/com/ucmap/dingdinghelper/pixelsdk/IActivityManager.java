package com.ucmap.dingdinghelper.pixelsdk;

import android.app.Activity;

/**
 * <b>@项目名：</b> Helmet<br>
 * <b>@包名：</b>com.ucmap.helmet<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> 宝诺科技<br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 */

public interface IActivityManager {

    <T extends Activity> void addActivity(T t);

    <T extends Activity> void removeActivity(T t);

    void removeAcitivtyByClazz(Class<? extends Activity> clazz);

}
