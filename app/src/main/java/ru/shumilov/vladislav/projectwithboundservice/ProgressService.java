package ru.shumilov.vladislav.projectwithboundservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class ProgressService extends Service {

    private final IBinder mBinder = new ProgressBinder();
    private Progress mProgress = new Progress();
    private CountPercentThread mCountPercentThread;
    private static final String TAG = CountPercentThread.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        mCountPercentThread = new CountPercentThread();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mCountPercentThread.quit();

        return super.onUnbind(intent);
    }

    public class ProgressBinder extends Binder {
        public ProgressService getService() {
            return ProgressService.this;
        }
    }

    public void startProgress() {
        if (mCountPercentThread.isAlive()) {
            mCountPercentThread.countPercents();
            return;
        }

        mCountPercentThread.start();
        mCountPercentThread.getLooper();
    }

    public class CountPercentThread extends HandlerThread {
        private boolean mHasQuit = false;
        private Handler mHandler;

        public CountPercentThread() {
            super(TAG);
        }

        public boolean quit() {
            mHasQuit = true;
            return super.quit();
        }

        @Override
        protected void onLooperPrepared() {
            mHandler = new Handler(getLooper());
            countPercents();
        }

        public void countPercents() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mProgress.setPercent(0);

                    while (mProgress.getPercent() != 100) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {

                        }

                        mProgress.setPercent(mProgress.getPercent() + 5);
                        mProgress.notifyObservers();
                    }
                }
            });
        }
    }

    public Progress getProgress() {
        return mProgress;
    }
}
