package com.example.waveview_simple;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.wavelibrary.WaveView;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private WaveView mWaveView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initParamsAndViews();

        initViews();
    }

    private void initParamsAndViews() {
        mContext = this;
    }

    private void initViews() {
        mWaveView = (WaveView) findViewById(R.id.waveview);
        mWaveView.setSimpleAnimatorToView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mWaveView.getWaveAnimatorHelper().startAnimators();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWaveView.getWaveAnimatorHelper().endAnimators();
    }
}
