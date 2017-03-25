// ITimingAidlInterface.aidl
package com.ucmap.dingdinghelper;
import com.ucmap.dingdinghelper.ITimerListener;

// Declare any non-default types here with import statements

interface ITimingAidlInterface {


       void registerTimerListener(ITimerListener timerListener);
       void unRegisterTimerListener(ITimerListener timerListener);
        void reInitCheckInTime(String time);
}
