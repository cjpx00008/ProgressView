package com.trinasolar.processviewdemo;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.trinasolar.library.ProgressView;

public class MainActivity extends AppCompatActivity {

    private ProgressView mProgressView;
    private Button mAnimBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressView = (ProgressView) findViewById(R.id.progress_view);
        mProgressView.setProgress(60);

        mAnimBtn = (Button) findViewById(R.id.anim_btn);
        mAnimBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressView.starAnim();
            }
        });
    }
}
