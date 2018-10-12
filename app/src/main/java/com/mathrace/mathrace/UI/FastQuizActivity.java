package com.mathrace.mathrace.UI;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.mathrace.mathrace.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FastQuizActivity extends AppCompatActivity implements RewardedVideoAdListener {

    @BindView(R.id.fast_quiz_tv_score)
    TextView mTvScore;
    @BindView(R.id.fast_quiz_btn_retry)
    Button retryButton;
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
    private static final String APP_ID = "ca-app-pub-3940256099942544~3347511713";
    private RewardedVideoAd rewardedVideoAd;
    String TAG = "FastQuizActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_quiz);
        ButterKnife.bind(this);
        try {
            this.getSupportActionBar().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();

    }

    private void loadRewardedVideoAd() {
        if (!rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.loadAd(AD_UNIT_ID, new AdRequest.Builder().build());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        rewardedVideoAd.resume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        rewardedVideoAd.pause(this);
    }

    private void showRewardedVideo() {
        if (rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.show();
        }
    }

    @OnClick(R.id.fast_quiz_btn_retry)
    void setRetryButton() {
        showRewardedVideo();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Log.e(TAG,"onRewardedVideoAdLoaded");
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Log.e(TAG,"onRewardedVideoAdOpened");
    }

    @Override
    public void onRewardedVideoStarted() {
        Log.e(TAG,"onRewardedVideoStarted");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Log.e(TAG,"onRewardedVideoAdClosed");
        loadRewardedVideoAd();

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Log.e(TAG,"onRewarded");
        loadRewardedVideoAd();

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Log.e(TAG,"onRewardedVideoAdLeftApplication");
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Log.e(TAG,"onRewardedVideoAdFailedToLoad");
    }

    @Override
    public void onRewardedVideoCompleted() {
        Log.e(TAG,"onRewardedVideoCompleted");
        mTvScore.setText("Yeah !");
    }
}
