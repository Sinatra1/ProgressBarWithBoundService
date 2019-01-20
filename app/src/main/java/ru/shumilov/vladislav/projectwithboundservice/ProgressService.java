package ru.shumilov.vladislav.projectwithboundservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class ProgressService extends Service {

    private final IBinder mBinder = new ProgressBinder();
    private int mPercent = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mPercent = 0;

        while (mPercent != 100) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {

            }

            mPercent += 5;
        }


        return mBinder;
    }

    public class ProgressBinder extends Binder {
        public ProgressService getService() {
            return ProgressService.this;
        }
    }

    public int getCurrentPercent() {
        return mPercent;
    }
}
