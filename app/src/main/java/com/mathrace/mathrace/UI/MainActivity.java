package com.mathrace.mathrace.UI;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.mathrace.mathrace.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.main_tv_score)
    TextView mTvScore;
    @BindView(R.id.main_iv_setting)
    ImageView mIvSettings;
    @BindView(R.id.main_btn_to_quiz)
    Button mBtnToFastQuiz;

    CountDownTimer mCountDownTimer;
    long mainCounter = 50000;
    long currentCounter = 0;
    boolean audioIsOn = true;
    Dialog mDialogStart, mDialogSettings;
    MediaPlayer mClick, mBackground;
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        try {
            this.getSupportActionBar().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        mClick = MediaPlayer.create(this, R.raw.button_click);
        mBackground = MediaPlayer.create(this, R.raw.audio);
        setDialogStart();
        setAdmob();


    }

    void doIn(long time) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        mCountDownTimer = new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {
                mTvScore.setText("Counter \n" + millisUntilFinished / 1000);
                currentCounter = millisUntilFinished;
            }

            public void onFinish() {
                Toast.makeText(MainActivity.this, "Game over !", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }


    @OnClick(R.id.main_iv_setting)
    void setIvSettings() {
        setDialogSettings();
    }

    @OnClick(R.id.main_btn_to_quiz)
    void toFastQuiz() {
        startActivity(new Intent(MainActivity.this, FastQuizActivity.class));
    }

    private void setDialogStart() {
        mDialogStart = new Dialog(this);
        mDialogStart.setContentView(R.layout.dialog_start);
        try {
            mDialogStart.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            mDialogStart.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Button mBtnStart = mDialogStart.findViewById(R.id.dialog_start_btn_start);
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doIn(mainCounter);
                mDialogStart.dismiss();
                mClick.start();
            }
        });
        mDialogStart.show();
    }

    private void setDialogSettings() {
        mDialogSettings = new Dialog(this);
        mDialogSettings.setContentView(R.layout.dialog_settings);
        try {
            mDialogSettings.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            mDialogSettings.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDialogSettings.setCancelable(true);
        final TextView mTvAudioStatus = mDialogSettings.findViewById(R.id.dialog_setting_tv_audio_status);
        final ImageView mIvAudio = mDialogSettings.findViewById(R.id.dialog_setting_iv_audio);
        ImageView mIvLang = mDialogSettings.findViewById(R.id.dialog_setting_iv_language);
        mIvAudio.getLayoutParams().height = 100;
        mIvAudio.getLayoutParams().width = 100;
        mIvLang.getLayoutParams().height = 100;
        mIvLang.getLayoutParams().width = 100;
        if (!audioIsOn) {
            mIvAudio.setImageDrawable(getResources().getDrawable(R.drawable.ic_mute));
            mTvAudioStatus.setText("Sound is Off");
        }
        Button mBtnSave = mDialogSettings.findViewById(R.id.dialog_setting_btn_save);
        mTvAudioStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (audioIsOn) {
                    if (mBackground.isPlaying()) {
                        mBackground.stop();
                    }

                    if (mClick.isPlaying()) {
                        mClick.stop();
                    }

                    mIvAudio.setImageDrawable(getResources().getDrawable(R.drawable.ic_mute));
                    mTvAudioStatus.setText("Sound is Off");
                    audioIsOn = false;

                } else {
                    mClick = MediaPlayer.create(MainActivity.this, R.raw.button_click);
                    mBackground = MediaPlayer.create(MainActivity.this, R.raw.audio);
                    mBackground.start();
                    mIvAudio.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume));
                    mTvAudioStatus.setText("Sound is On");

                    audioIsOn = true;

                }

            }
        });
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialogSettings.dismiss();
                if (audioIsOn) {
                    mClick.start();
                }
            }
        });
        mDialogSettings.show();
    }

    private void setAdmob() {
        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }


}

