package com.ucmap.dingdinghelper.pixelsdk;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.ucmap.dingdinghelper.R;

/**
 * <b>@项目名：</b> Helmet<br>
 * <b>@包名：</b>com.ucmap.helmet<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> 宝诺科技<br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 */

public class PointActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point);
        ActivityManager.getInstance().addActivity(this);
        Window mWindow = this.getWindow();
        mWindow.setGravity(Gravity.START | Gravity.TOP);
        WindowManager.LayoutParams mLayoutParams = mWindow.getAttributes();
        mLayoutParams.width = 1;
        mLayoutParams.height = 1;

        mLayoutParams.x = 0;
        mLayoutParams.y = 0;

        mWindow.setAttributes(mLayoutParams);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getInstance().removeActivity(this);
    }
}
