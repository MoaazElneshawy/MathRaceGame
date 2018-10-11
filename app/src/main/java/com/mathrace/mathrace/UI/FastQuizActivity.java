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
import com.mathrace.mathrace.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FastQuizActivity extends AppCompatActivity {

    @BindView(R.id.fast_quiz_tv_score)
    TextView mTvScore;
    @BindView(R.id.fast_quiz_btn_retry)
    Button retryButton;

    private static final long GAME_LENGTH_MILLISECONDS = 3000;
    private InterstitialAd interstitialAd;
    private CountDownTimer countDownTimer;
    private boolean gameIsInProgress;
    private long timerMilliseconds;
    AdRequest adRequest;
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

        interstitialAd = new InterstitialAd(this);

        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);

        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
            Log.e(TAG, "show");
        }


        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                startGame();
            }
        });

    }


    private void createTimer(final long milliseconds) {
        // Create the game timer, which counts down to the end of the level
        // and shows the "retry" button.
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(milliseconds, 50) {
            @Override
            public void onTick(long millisUnitFinished) {
                timerMilliseconds = millisUnitFinished;
                mTvScore.setText("seconds remaining: " + ((millisUnitFinished / 1000) + 1));
            }

            @Override
            public void onFinish() {
                gameIsInProgress = false;
                mTvScore.setText("done!");
                retryButton.setVisibility(View.VISIBLE);
            }
        };
    }

    @Override
    public void onResume() {
        // Start or resume the game.
        super.onResume();

        if (gameIsInProgress) {
            resumeGame(timerMilliseconds);
        }
    }

    @Override
    public void onPause() {
        // Cancel the timer if the game is paused.
        if (countDownTimer != null)
            countDownTimer.cancel();
        super.onPause();
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
            startGame();
        }
    }

    private void startGame() {
        // Request a new ad if one isn't already loaded, hide the button, and kick off the timer.
        if (!interstitialAd.isLoading() && !interstitialAd.isLoaded()) {
            adRequest = new AdRequest.Builder().build();
            interstitialAd.loadAd(adRequest);
        }

        retryButton.setVisibility(View.INVISIBLE);
        resumeGame(GAME_LENGTH_MILLISECONDS);
    }

    private void resumeGame(long milliseconds) {
        // Create a new timer for the correct length and start it.
        gameIsInProgress = true;
        timerMilliseconds = milliseconds;
        createTimer(milliseconds);
        countDownTimer.start();
    }

    @OnClick(R.id.fast_quiz_btn_retry)
    void setRetryButton() {
        showInterstitial();
    }
}
