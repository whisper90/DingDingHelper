package com.ucmap.dingdinghelper.ui;

import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ucmap.dingdinghelper.ITimerListener;
import com.ucmap.dingdinghelper.ITimingAidlInterface;
import com.ucmap.dingdinghelper.R;
import com.ucmap.dingdinghelper.app.App;
import com.ucmap.dingdinghelper.entity.AccountEntity;
import com.ucmap.dingdinghelper.pixelsdk.ActivityManager;
import com.ucmap.dingdinghelper.pixelsdk.PixelActivityUnion;
import com.ucmap.dingdinghelper.pixelsdk.PointActivity;
import com.ucmap.dingdinghelper.services.TimingService;
import com.ucmap.dingdinghelper.sphelper.SPUtils;
import com.ucmap.dingdinghelper.utils.Constants;
import com.ucmap.dingdinghelper.utils.DingHelperUtils;
import com.ucmap.dingdinghelper.utils.JsonUtils;
import com.ucmap.dingdinghelper.utils.ShellUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.ucmap.dingdinghelper.utils.Constants.AFTERNOON_CHECK_IN_TIME;
import static com.ucmap.dingdinghelper.utils.Constants.MORNING_CHECK_IN_TIME;

public class MainActivity extends AppCompatActivity {

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case 0x001:
                    updateUI((String) msg.obj);
                    break;
            }
            return false;
        }
    });

    private TextView mPTimeTextView;

    private Runnable m = new Runnable() {
        @Override
        public void run() {
            if (mFrameLayout != null)
                mFrameLayout.setVisibility(View.GONE);
        }
    };

    private void updateUI(String time) {
        mHandler.removeCallbacks(m);
        if (mFrameLayout != null)
            mFrameLayout.setVisibility(View.VISIBLE);

        if (mPTimeTextView != null) {
            mPTimeTextView.setVisibility(View.VISIBLE);
            mPTimeTextView.setText(time);
        }
        mHandler.postDelayed(m, 30 * 1000);
    }

    private ITimingAidlInterface mITimingAidlInterface;
    private ITimerListener mITimerListener = new ITimerListener.Stub() {

        @Override
        public void toCallback(String time) throws RemoteException {
            mHandler.obtainMessage(0x001, time).sendToTarget();
        }
    };
    private boolean isBindServices = false;
    private ServiceConnection mServiceConnection = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                isBindServices = true;
                mITimingAidlInterface = ITimingAidlInterface.Stub.asInterface(service);
                mITimingAidlInterface.registerTimerListener(mITimerListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private FrameLayout mFrameLayout;

    private TimePickerDialog mTimePickerDialog;
    private TextView mTimeTextView;


    private TextView mNTimeTextView;

    private void showPickerMorning() {

        if (mTimePickerDialog == null) {
            mTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                    if (hourOfDay > 12) {
                        Toast.makeText(App.mContext, "早上打卡时间不能超过12点", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SPUtils.save(MORNING_CHECK_IN_TIME, hourOfDay + ":" + minute);
                    setAlarmList(getAccountLists());
                    if (mTimeTextView != null)
                        mTimeTextView.setText(hourOfDay + ":" + minute);
                    toModifyNotifyTime(hourOfDay + ":" + minute);
                }
            }, 8, 45, true);
        }
        mTimePickerDialog.show();
    }

    private Executor mComminicationPool = Executors.newCachedThreadPool();

    private void toModifyNotifyTime(final String time) {

        mComminicationPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mITimingAidlInterface != null)
                        mITimingAidlInterface.reInitCheckInTime(time);
                } catch (Exception e) {

                }

            }
        });
    }

    private TimePickerDialog mNoonTimePickerDialog = null;

    private List<AccountEntity> getAccountLists() {
        return JsonUtils.fromJsonList((String) SPUtils.getString(Constants.ACCOUNT_LIST, "-1"), AccountEntity.class);
    }

    private void showPickerAfterNoon() {
        if (mNoonTimePickerDialog == null) {
            mNoonTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    if (hourOfDay < 12) {
                        Toast.makeText(App.mContext, "下班打卡需要12点以后", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SPUtils.save(AFTERNOON_CHECK_IN_TIME, hourOfDay + ":" + minute);
                    setAlarmList(getAccountLists());
                    if (mNTimeTextView != null)
                        mNTimeTextView.setText(hourOfDay + ":" + minute);
                    toModifyNotifyTime(hourOfDay + ":" + minute);
                }
            }, 20, 45, true);
        }
        mNoonTimePickerDialog.show();
    }


    private void initAccount() {
        String jsonAccountList = (String) SPUtils.getString(Constants.ACCOUNT_LIST, "-1");
        List<AccountEntity> mAccountEntities = JsonUtils.listJson(jsonAccountList, AccountEntity.class);

        Log.i("Infoss", "size:" + mAccountEntities.toString());
        if (mAccountEntities == null || mAccountEntities.isEmpty()) {
            this.findViewById(R.id.check_time_linearLayout).setVisibility(View.GONE);
            mClearButton.setVisibility(View.GONE);
        } else {
            mClearButton.setVisibility(View.VISIBLE);
            if (!isRunning(TimingService.class.getName()))
                startService(new Intent(this, TimingService.class));
            this.bindService(new Intent(this, TimingService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
            this.findViewById(R.id.check_time_linearLayout).setVisibility(View.VISIBLE);
        }
        setAlarmList(mAccountEntities);
    }

    private void clearAccount() {

        SPUtils.clear();
        List<String> mList = new ArrayList<>();
        mList.add(Constants.POINT_SERVICES_ORDER);
        mList.add(Constants.DISENABLE_SERVICE_PUT);
        ShellUtils.execCmd(mList, true);
        if (mServiceConnection != null && isBindServices) {
            this.unbindService(mServiceConnection);
            isBindServices = false;
        }
        if (isRunning(TimingService.class.getName()))
            stopService(new Intent(this, TimingService.class));

        if (mFrameLayout != null) {
            mFrameLayout.setVisibility(View.GONE);
        }
        initAccount();

    }

    private boolean isRunning(String className) {

        boolean tag = false;
        android.app.ActivityManager mActivityManager = (android.app.ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<android.app.ActivityManager.RunningServiceInfo> mRunningTaskInfos = mActivityManager.getRunningServices(50);
        Log.i("Infoss", "  size:" + mRunningTaskInfos.size());
        for (int i = 0; i < mRunningTaskInfos.size(); i++) {
            Log.i("Infoss", "RunningInfo:" + mRunningTaskInfos.get(i).service.getClassName());
            if (mRunningTaskInfos.get(i).service.getClassName().equals(className)) {
                return true;
            }
        }
        return tag;

    }

    private Button mClearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mFrameLayout = (FrameLayout) this.findViewById(R.id.parentGroup_frameLayout);
        mPTimeTextView = (TextView) this.findViewById(R.id.p_time_textView_);

        /*if (!isRunning(TimingService.class.getName())) {
            Log.i("Infoss", "已经启动service");
            startService(new Intent(this, TimingService.class));
        }*/
        String id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        mClearButton = (Button) this.findViewById(R.id.clear_account);

        Log.i("Infoss", "  android _id:" + id);

        initAccount();

        mNTimeTextView = (TextView) this.findViewById(R.id.n_time_textView);
        this.findViewById(R.id.n_time_textView)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPickerAfterNoon();

                    }
                });

        mTimeTextView = (TextView) this.findViewById(R.id.time_textView);
        this.findViewById(R.id.select_check_in_time)//
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPickerMorning();
                    }
                });

        this.findViewById(R.id.clear_account)//
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mClearButton.setVisibility(View.GONE);
                        clearAccount();
                        Toast.makeText(App.mContext, "已经清空账户", Toast.LENGTH_SHORT).show();
                    }
                });

        PixelActivityUnion
                .with(App.mContext)
                .targetActivityClazz(PointActivity.class)//
                .args(null)//
                .setActiviyManager(ActivityManager.getInstance())
                .start();


        this.findViewById(R.id.save_password)//
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mIntent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivityForResult(mIntent, 0x88);
                    }
                });
        this.findViewById(R.id.open_service)//
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivity(intent);
                    }
                });

        //当前应用的代码执行目录
        if (ShellUtils.upgradeRootPermission(getPackageCodePath())) {
//            Toast.makeText(App.mContext, "已经拿到root权限", Toast.LENGTH_SHORT).show();

//            Log.i("Infoss","shell result:"+ShellUtils.execCmd("input keyevent 26", true).toString());
        } else {
            Toast.makeText(App.mContext, "root权限失败.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("Infoss", "回调  request code:" + requestCode + "   resultCode:" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x88 && resultCode == RESULT_OK) {
            initAccount();
        }
    }

    private void setAlarmList(List<AccountEntity> mList) {

        if (mList == null || mList.isEmpty()) {
            return;
        }
        for (AccountEntity mAccountEntity : mList) {
            DingHelperUtils.setAlarm(mAccountEntity, App.mContext);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServiceConnection != null && isBindServices) {
            this.unbindService(mServiceConnection);
            isBindServices = false;
        }
        mHandler.removeCallbacksAndMessages(null);
        try {
            if (mITimingAidlInterface != null)
                mITimingAidlInterface.unRegisterTimerListener(mITimerListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
