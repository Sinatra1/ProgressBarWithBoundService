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
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer {
    public ProgressService mProgressService;
    private boolean mBound = false;
    private Progress mProgress;
    private TextView mPercentTextView;
    private Button mStartServiceButton;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPercentTextView = findViewById(R.id.tvPercentView);
        mStartServiceButton = findViewById(R.id.btnStartService);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(MainActivity.this, ProgressService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
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
                mPercentTextView.setText(mProgress.getPercent().toString());
            }
        });
    }
}
