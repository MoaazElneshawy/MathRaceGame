package com.mathrace.mathrace.UI;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.mathrace.mathrace.Models.TaskModel;
import com.mathrace.mathrace.R;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.mathrace.mathrace.Utilities.BaseActivity;
import com.mathrace.mathrace.Utilities.MathRaceQuestionsGenerator;
import com.mathrace.mathrace.Utilities.SharedPrefUtilities;

public class MainActivity extends BaseActivity implements RewardedVideoAdListener {


    @BindView(R.id.main_tv_score)
    TextView tvScore;
    @BindView(R.id.main_iv_setting)
    ImageView ivSettings;
    @BindView(R.id.main_btn_to_quiz)
    Button btnToFastQuiz;
    @BindView(R.id.main_btn_answer1)
    Button btnAnswer1;
    @BindView(R.id.main_btn_answer2)
    Button btnAnswer2;
    @BindView(R.id.main_btn_answer3)
    Button btnAnswer3;
    @BindView(R.id.main_btn_answer4)
    Button btnAnswer4;
    @BindView(R.id.main_tv_question)
    TextView tvQuestion;
    @BindView(R.id.main_layout)
    RelativeLayout mainLayout;
    @BindView(R.id.main_tv_timer)
    TextView tvTimer;
    CountDownTimer mCountDownTimer;
    long mainCounter = 10000, currentCounter = 0, moreTime;
    int current_level = 1, number_of_questions = 1, watched_video_count = 0, played_quiz_count;
    public static final int REQUEST_CODE = 10;
    Dialog mDialogStart, mDialogSettings, mDialogGameOver;
    MediaPlayer mClick, mBackground;
    private AdView mAdView;
    private InterstitialAd interstitialAd;
    private RewardedVideoAd rewardedVideoAd;
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
    AdRequest adRequest;
    TaskModel model;
    public static final String TAG = "MainActivity";
    boolean isCorrect, stop = false, sound, isStarted = false;
    int score = 0;
    SharedPrefUtilities sharedPrefUtilities;
    String language;

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
        sharedPrefUtilities = new SharedPrefUtilities(this);
        sound = sharedPrefUtilities.getAUDIO();
        played_quiz_count = sharedPrefUtilities.getPlayedQuiz();
        watched_video_count = sharedPrefUtilities.getWatchedVideo();
        mClick = MediaPlayer.create(this, R.raw.button_click);
        mBackground = MediaPlayer.create(this, R.raw.audio);
        setDialogStart();
        setAdmob();

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);

        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
    }


    void doIn(long time) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        mCountDownTimer = new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {
                tvScore.setText(getResources().getString(R.string.score) + " : " + score + "\n" + getResources().getString(R.string.level) + "  : " + current_level);
                currentCounter = millisUntilFinished;
                btnAnswer1.setEnabled(true);
                btnAnswer2.setEnabled(true);
                btnAnswer3.setEnabled(true);
                btnAnswer4.setEnabled(true);
                tvTimer.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                tvTimer.setText("0");
                tvScore.setText(getResources().getString(R.string.score) + " : " + score + "\n" + getResources().getString(R.string.timer) + " : " + current_level);
                Toast.makeText(MainActivity.this, "Game over !", Toast.LENGTH_SHORT).show();
                btnAnswer1.setEnabled(false);
                btnAnswer2.setEnabled(false);
                btnAnswer3.setEnabled(false);
                btnAnswer4.setEnabled(false);
                stop = true;
                setDialogGameOver(String.valueOf(score));
            }
        }.start();
    }


    @OnClick(R.id.main_iv_setting)
    void setIvSettings() {
        setDialogSettings();
    }

    @OnClick(R.id.main_btn_to_quiz)
    void toFastQuiz() {
//        getMorePoints();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!interstitialAd.isLoaded()) {
            adRequest = new AdRequest.Builder().build();
            interstitialAd.loadAd(adRequest);
        }
        rewardedVideoAd.resume(this);
        if (!isStarted) {
            if (mDialogStart == null) {
                setDialogStart();
            } else {
                mDialogStart.show();
            }
        }
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

    private void getMorePoints() {
        interstitialAd.show();
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Intent intent = new Intent(MainActivity.this, FastQuizActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                moreTime = data.getLongExtra("result", 50000);
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Oops !", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setDialogStart() {
        mDialogStart = new Dialog(this);
        mDialogStart.setContentView(R.layout.dialog_start);
        mDialogStart.setCancelable(false);
        try {
            mDialogStart.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
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
                getNewQuestion(0);
                isStarted = true;
            }
        });
        mDialogStart.show();
    }

    private void setDialogGameOver(String score) {
        mDialogGameOver = new Dialog(this);
        mDialogGameOver.setContentView(R.layout.dialog_game_over);
        try {
            mDialogGameOver.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            mDialogGameOver.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDialogGameOver.setCancelable(false);
        TextView tvScore = mDialogGameOver.findViewById(R.id.dialog_game_tv_score);
        Button btnStartAgain = mDialogGameOver.findViewById(R.id.dialog_game_btn_start);
        Button btnGetPoints = mDialogGameOver.findViewById(R.id.dialog_game_btn_more_points);
        Button btnWatchVideo = mDialogGameOver.findViewById(R.id.dialog_game_btn_watch_video);

        if (watched_video_count == 5 || (!rewardedVideoAd.isLoaded())) {
            btnWatchVideo.setVisibility(View.GONE);
        }
        tvScore.setText("You got " + score);
        btnStartAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doIn(mainCounter);
                mDialogGameOver.dismiss();
                mClick.start();
                getNewQuestion(0);
            }
        });
        btnGetPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialogGameOver.dismiss();
//                getMorePoints();
            }
        });
        btnWatchVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rewardedVideoAd.isLoaded()) {
                    showRewardedVideo();
                    mDialogGameOver.dismiss();
                }
            }
        });
        mDialogGameOver.show();

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
        mDialogSettings.setCancelable(false);
        ImageView mIvAudio = mDialogSettings.findViewById(R.id.dialog_setting_iv_audio);
        ImageView mIvLang = mDialogSettings.findViewById(R.id.dialog_setting_iv_language);
        mIvAudio.getLayoutParams().height = 100;
        mIvAudio.getLayoutParams().width = 100;
        mIvLang.getLayoutParams().height = 100;
        mIvLang.getLayoutParams().width = 100;
        Button btnSave = mDialogSettings.findViewById(R.id.dialog_setting_btn_save);
        final RadioButton rbSoundOn = mDialogSettings.findViewById(R.id.dialog_setting_rb_audio_on);
        RadioButton rbSoundOff = mDialogSettings.findViewById(R.id.dialog_setting_rb_audio_off);
        final RadioButton rbLangEn = mDialogSettings.findViewById(R.id.dialog_setting_rb_lang_en);
        RadioButton rbLangAr = mDialogSettings.findViewById(R.id.dialog_setting_rb_lang_ar);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rbLangEn.isSelected() || rbLangEn.isChecked()) {
                    language = "en";
                } else {
                    language = "ar";
                }

                if (rbSoundOn.isChecked() || rbSoundOn.isSelected()) {
                    sound = true;
                } else {
                    sound = false;
                }
                sharedPrefUtilities.setAudio(sound);
                Log.e(TAG, "language >> " + language);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                    setApplicationLanguage(language);
                } else {
                    languageConfiguration(language);
                }
                mDialogSettings.dismiss();
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

    @OnClick({R.id.main_btn_answer1, R.id.main_btn_answer2, R.id.main_btn_answer3, R.id.main_btn_answer4})
    public void doCheck(View view) {

        String value1 = "0";
        String value2 = "1";
        String value3 = "2";
        String value4 = "3";
        String selected_value;
        Button selected;
        int id = view.getId();
        if (id == R.id.main_btn_answer1) {
            selected_value = value1;
            selected = btnAnswer1;
        } else if (id == R.id.main_btn_answer2) {
            selected = btnAnswer2;
            selected_value = value2;
        } else if (id == R.id.main_btn_answer3) {
            selected_value = value3;
            selected = btnAnswer3;
        } else {
            selected = btnAnswer4;
            selected_value = value4;
        }
        checkAnswers(value1, value2, value3, value4, getModel(), current_level, selected, selected_value);
    }

    private void checkAnswers(String value1, String value2, String value3, String value4,
                              TaskModel model, final int current_level, Button selected, String selected_value) {

        Log.e(TAG, selected_value + " - " + model.getCorrect_answer());
        if (selected_value.equals(model.getCorrect_answer())) {
            selected.setBackground(getResources().getDrawable(R.drawable.correct_answer_background));
            isCorrect = true;
            score++;
        } else {
            isCorrect = false;
            if (value1.equals(model.getCorrect_answer())) {
                btnAnswer1.setBackground(getResources().getDrawable(R.drawable.correct_answer_background));
            } else if (value2.equals(model.getCorrect_answer())) {
                btnAnswer2.setBackground(getResources().getDrawable(R.drawable.correct_answer_background));
            } else if (value3.equals(model.getCorrect_answer())) {
                btnAnswer3.setBackground(getResources().getDrawable(R.drawable.correct_answer_background));
            } else {
                btnAnswer4.setBackground(getResources().getDrawable(R.drawable.correct_answer_background));
            }
            selected.setBackground(getResources().getDrawable(R.drawable.wrong_answer_background));
        }
        if (isCorrect) {
            currentCounter += 1000;
            doIn(currentCounter);
        }
        if (!stop) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getNewQuestion(current_level);
                }
            }, 500);
        }
    }

    private void getNewQuestion(int current_level) {
        btnAnswer1.setBackground(getResources().getDrawable(R.drawable.answers_background));
        btnAnswer2.setBackground(getResources().getDrawable(R.drawable.answers_background));
        btnAnswer3.setBackground(getResources().getDrawable(R.drawable.answers_background));
        btnAnswer4.setBackground(getResources().getDrawable(R.drawable.answers_background));

        List<String> list = MathRaceQuestionsGenerator.getQuestion(current_level);
        TaskModel model = new TaskModel();
        model.setQuestion(list.get(0) + "  " + list.get(1) + "  " + list.get(2));
        model.setAnswer1(list.get(4));
        model.setAnswer2(list.get(5));
        model.setAnswer3(list.get(6));
        model.setAnswer4(list.get(7));
        model.setCorrect_answer(list.get(8));

        tvQuestion.setText(model.getQuestion());
        btnAnswer1.setText(model.getAnswer1());
        btnAnswer2.setText(model.getAnswer2());
        btnAnswer3.setText(model.getAnswer3());
        btnAnswer4.setText(model.getAnswer4());
        setModel(model);
        number_of_questions++;
        startNewLevel();
    }

    public TaskModel getModel() {
        return model;
    }

    public void setModel(TaskModel model) {
        this.model = model;
    }

    private void startNewLevel() {
        if (number_of_questions == 10) {
            current_level++;
            Toast.makeText(this, "Level " + current_level, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDialogGameOver != null) {
            mDialogGameOver.cancel();
            mDialogGameOver = null;
        }
        if (mDialogSettings != null) {
            mDialogSettings.cancel();
            mDialogSettings = null;
        }
        if (mDialogStart != null) {
            mDialogStart.cancel();
            mDialogStart = null;
        }
    }

    private void loadRewardedVideoAd() {
        if (!rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.loadAd(AD_UNIT_ID, new AdRequest.Builder().build());
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Log.e(TAG, "onRewardedVideoAdLoaded");

    }

    @Override
    public void onRewardedVideoAdOpened() {
        Log.e(TAG, "onRewardedVideoAdOpened");
        watched_video_count++;
        sharedPrefUtilities.setWatchedVideo(watched_video_count);
        loadRewardedVideoAd();

    }

    @Override
    public void onRewardedVideoStarted() {
        Log.e(TAG, "onRewardedVideoStarted");
        mDialogGameOver.cancel();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Log.e(TAG, "onRewardedVideoAdClosed");
        loadRewardedVideoAd();
//        mDialogGameOver.show();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Log.e(TAG, "onRewarded");
        loadRewardedVideoAd();
        doIn(5000);
        getNewQuestion(current_level);
        Toast.makeText(this, "You got 5 seconds more", Toast.LENGTH_SHORT).show();
        stop = false;
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Log.e(TAG, "onRewardedVideoAdLeftApplication");
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Log.e(TAG, "onRewardedVideoAdFailedToLoad");
    }

    @Override
    public void onRewardedVideoCompleted() {
        Log.e(TAG, "onRewardedVideoCompleted");
        loadRewardedVideoAd();
    }

    private void setApplicationLanguage(String newLanguage) {
        Resources activityRes = getResources();
        Configuration activityConf = activityRes.getConfiguration();
        Locale newLocale = new Locale(newLanguage);
        activityConf.setLocale(newLocale);
        activityRes.updateConfiguration(activityConf, activityRes.getDisplayMetrics());

        Resources applicationRes = getApplicationContext().getResources();
        Configuration applicationConf = applicationRes.getConfiguration();
        applicationConf.setLocale(newLocale);
        applicationRes.updateConfiguration(applicationConf,
                applicationRes.getDisplayMetrics());

        sharedPrefUtilities.setLanguage(newLanguage);
        recreate();
    }

    private void languageConfiguration(String language) {

        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        sharedPrefUtilities.setLanguage(language);
        recreate();
    }

}

