package ru.shumilov.vladislav.projectwithboundservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer {
    public ProgressService mProgressService;
    private boolean mBound = false;
    private Progress mProgress;
    private Handler mHandler = new Handler();

    private Button mStartServiceButton;
    private ProgressBar mProgressBar;
    private Button mFlushProgressBarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartServiceButton = findViewById(R.id.btnStartService);
        mProgressBar = findViewById(R.id.progressBar);
        mFlushProgressBarButton = findViewById(R.id.btnFlushProgressBar);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(MainActivity.this, ProgressService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

        mProgressBar.setVisibility(ProgressBar.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mStartServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound) {
                    mProgressService.startProgress();
                }
            }
        });

        mFlushProgressBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBound) {
                    return;
                }

                int flushPercent = 0;

                if (mProgress.getPercent() > 50) {
                    flushPercent = mProgress.getPercent() - 50;
                }

                mProgress.setPercent(flushPercent);
            }
        });
    }

    @Override
    protected void onStop() {
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }

        super.onStop();
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            ProgressService.ProgressBinder binder = (ProgressService.ProgressBinder) service;
            mProgressService = binder.getService();
            mProgress = mProgressService.getProgress();
            mProgress.addObserver(MainActivity.this);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    @Override
    public void update(Observable observable, Object arg) {
        if (!(observable instanceof Progress)) {
            return;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mProgressBar.getVisibility() != View.VISIBLE) {
                    mProgressBar.setVisibility(ProgressBar.VISIBLE);
                }

                mProgressBar.setProgress(mProgress.getPercent());
            }
        });
    }
}
