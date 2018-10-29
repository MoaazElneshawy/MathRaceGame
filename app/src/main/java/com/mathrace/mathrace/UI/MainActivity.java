package com.mathrace.mathrace.UI;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
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
    @BindView(R.id.main_quiz_layout)
    LinearLayout layoutQuiz;
    @BindView(R.id.main_game_over_layout)
    LinearLayout layoutGameOver;
    //    @BindView(R.id.main_btn_start)
//    Button btnStart;
    @BindView(R.id.main_game_over_btn_watch_video)
    Button btnWatchVideo;
    @BindView(R.id.main_game_over_btn_to_quiz)
    Button btnPlayQuiz;
    @BindView(R.id.main_game_over_btn_start_again)
    Button btnStartAgain;
    @BindView(R.id.main_game_over_btn_share_on_fb)
    Button btnShareOnFb;
    @BindView(R.id.main_game_over_tv_score)
    TextView tvGameOverScore;
    @BindView(R.id.main_game_over_tv_quiz_hint)
    TextView tvQuizHint;
    @BindView(R.id.main_game_over_tv_video_hint)
    TextView tvVideoHint;
    @BindView(R.id.main_game_over_iv_status)
    ImageView ivPlayerStatus;

    CallbackManager callbackManager;
    ShareDialog shareDialog;
    CountDownTimer mCountDownTimer;
    long mainCounter = 10000, currentCounter = 0, pauseCounter = 0, mLastClickTime = 0;
    int current_level = 1, questions_score = 0, watched_video_count = 0, played_quiz_count = 0, score = 0, bg_audio_position = 0;
    public static final int REQUEST_CODE = 10;
    Dialog mDialogSettings, mDialogBack;
    MediaPlayer mClick, mBackground;
    private AdView mAdView;
    private InterstitialAd interstitialAd;
    private RewardedVideoAd rewardedVideoAd;
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
    AdRequest adRequest;
    TaskModel model;
    public static final String TAG = "MainActivity";
    boolean isCorrect, sound, bg_sound, isNewHighScore, gameFinished = true, gamePaused = false, pauseByVideo = false;
    SharedPrefUtilities sharedPrefUtilities;
    String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        try {
            this.getSupportActionBar().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
        callbackManager = CallbackManager.Factory.create();
        settingFB();

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        sharedPrefUtilities = new SharedPrefUtilities(this);
//        onChangeSoundListener = (OnChangeSoundListener) getApplicationContext();
        sound = sharedPrefUtilities.getAUDIO();
        played_quiz_count = sharedPrefUtilities.getPlayedQuiz();
        watched_video_count = sharedPrefUtilities.getWatchedVideo();
        if (watched_video_count == 3) {
            tvVideoHint.setVisibility(View.GONE);
            btnWatchVideo.setVisibility(View.GONE);
        }
        mClick = MediaPlayer.create(MainActivity.this, R.raw.button_click);
        mBackground = MediaPlayer.create(this, R.raw.background_audio);
        mBackground.setLooping(true);
        Intent i = this.getIntent();
        if (i != null && i.hasExtra("audio_position")) {
            bg_audio_position = i.getIntExtra("audio_position", 0);
            if (bg_audio_position != 0) {
                mBackground.seekTo(bg_audio_position);
            }
        }
        if (sharedPrefUtilities.getBgAudio()) {
            mBackground.start();
        }
        if (i.hasExtra("score")) {
            int saved_score = i.getIntExtra("score", 0);
            long time = i.getLongExtra("time", 0l);
            int saved_level = i.getIntExtra("level", 0);
            Log.e("FQ " + TAG, "intent " + saved_score + " - " + time + " " + saved_level);
            if (time > 0) {
                savedDoIn(time);
                score = saved_score;
                current_level = saved_level;
                layoutQuiz.setVisibility(View.VISIBLE);
                getNewQuestion(saved_level);
            } else {
                layoutQuiz.setVisibility(View.GONE);
                layoutGameOver.setVisibility(View.VISIBLE);
                tvGameOverScore.setText(R.string.quiz_failed);
                ivPlayerStatus.setImageResource(R.drawable.ic_loser);
            }
        } else {
            doIn(mainCounter);
            getNewQuestion(current_level);
            layoutQuiz.setVisibility(View.VISIBLE);
        }
        setAdmob();


        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);

        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();


    }


    void doIn(final long time) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        mCountDownTimer = new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {
                tvScore.setText(getResources().getString(R.string.score) + " : " + score +
                        "\n" + getResources().getString(R.string.level) + "  : " + current_level);
                currentCounter = millisUntilFinished;
                btnAnswer1.setEnabled(true);
                btnAnswer2.setEnabled(true);
                btnAnswer3.setEnabled(true);
                btnAnswer4.setEnabled(true);
                tvTimer.setText("" + millisUntilFinished / 1000);
                tvScore.setVisibility(View.VISIBLE);
                gameFinished = false;
            }

            public void onFinish() {
                int s = getHighScore(score);
                tvTimer.setText("0");
                tvScore.setText(getResources().getString(R.string.score) + " : " + score +
                        "\n" + getResources().getString(R.string.level) + " : " + current_level);
                tvScore.setVisibility(View.GONE);
                btnAnswer1.setEnabled(false);
                btnAnswer2.setEnabled(false);
                btnAnswer3.setEnabled(false);
                btnAnswer4.setEnabled(false);
//                ivSettings.setVisibility(View.VISIBLE);
                currentCounter = 0l;

                gameFinished = true;
                if (isNewHighScore) {
                    ivPlayerStatus.setImageResource(R.drawable.ic_winner);
                    tvGameOverScore.setText(getResources().getString(R.string.new_high_score) + " ( " + score + " )");
                } else {
                    ivPlayerStatus.setImageResource(R.drawable.ic_loser);
                    tvGameOverScore.setText(getResources().getString(R.string.game_over_score) + " ( " + score + " ) \n"
                            + getResources().getString(R.string.last_high_score) + " ( " + s + " )");
                }
                if ((!rewardedVideoAd.isLoaded()) || (sharedPrefUtilities.getWatchedVideo() == 3)) {
                    btnWatchVideo.setVisibility(View.GONE);
                    tvVideoHint.setVisibility(View.GONE);
                } else {
                    btnWatchVideo.setVisibility(View.VISIBLE);
                    tvVideoHint.setVisibility(View.VISIBLE);
                }
                if (sharedPrefUtilities.getPlayedQuiz() == 5) {
                    btnPlayQuiz.setVisibility(View.GONE);
                    tvQuizHint.setVisibility(View.GONE);
                }

//                if (!pause)
                performAnimation(layoutQuiz, layoutGameOver);
            }
        }.start();


    }

    void savedDoIn(final long time) {

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        mCountDownTimer = new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {
                tvScore.setText(getResources().getString(R.string.score) + " : " + score +
                        "\n" + getResources().getString(R.string.level) + "  : " + current_level);
                currentCounter = millisUntilFinished;
                btnAnswer1.setEnabled(true);
                btnAnswer2.setEnabled(true);
                btnAnswer3.setEnabled(true);
                btnAnswer4.setEnabled(true);
                tvTimer.setText("" + millisUntilFinished / 1000);
                gameFinished = false;
                tvScore.setVisibility(View.VISIBLE);
            }

            public void onFinish() {
                int s = getHighScore(score);
                tvTimer.setText("0");
                tvScore.setText(getResources().getString(R.string.score) + " : " + score +
                        "\n" + getResources().getString(R.string.level) + " : " + current_level);
                tvScore.setVisibility(View.GONE);
                currentCounter = 0l;
                btnAnswer1.setEnabled(false);
                btnAnswer2.setEnabled(false);
                btnAnswer3.setEnabled(false);
                btnAnswer4.setEnabled(false);
                gameFinished = true;
                if (isNewHighScore) {
                    ivPlayerStatus.setImageResource(R.drawable.ic_winner);
                    tvGameOverScore.setText(getResources().getString(R.string.new_high_score) + " ( " + score + " )");
                } else {
                    ivPlayerStatus.setImageResource(R.drawable.ic_loser);
                    tvGameOverScore.setText(getResources().getString(R.string.game_over_score) + " ( " + score + " ) \n"
                            + getResources().getString(R.string.last_high_score) + " ( " + s + " )");
                }

                if ((!rewardedVideoAd.isLoaded()) || (sharedPrefUtilities.getWatchedVideo() == 3)) {
                    btnWatchVideo.setVisibility(View.GONE);
                    tvVideoHint.setVisibility(View.GONE);
                } else {
                    btnWatchVideo.setVisibility(View.VISIBLE);
                    tvVideoHint.setVisibility(View.VISIBLE);
                }
                if (sharedPrefUtilities.getPlayedQuiz() == 5) {
                    btnPlayQuiz.setVisibility(View.GONE);
                    tvQuizHint.setVisibility(View.GONE);
                }
                if (time == 0) {
                    btnWatchVideo.setVisibility(View.GONE);
                    tvVideoHint.setVisibility(View.GONE);
                    btnPlayQuiz.setVisibility(View.GONE);
                    tvQuizHint.setVisibility(View.GONE);
                }

                performAnimation(layoutQuiz, layoutGameOver);
            }
        }.start();


    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    void settingFB() {
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                doIn(mainCounter);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
                Log.e(TAG, e.getMessage());
            }
        });

    }

    @OnClick(R.id.main_game_over_btn_share_on_fb)
    void shareOnFB() {

        if (ShareDialog.canShow(ShareLinkContent.class)) {

            ShareContent content = new ShareLinkContent.Builder()

                    .setQuote((getString(R.string.score) + " " + score + " played on " + "Math-Race"))
                    .setContentUrl(Uri.parse("https://www.google.com"))

                    .setShareHashtag(new ShareHashtag.Builder()
                            .setHashtag("#MathRace")
                            .build())

                    .build();

            shareDialog.show(content);

        }
    }

    private void performAnimation(final View viewToHide, final View viewToShow) {
        viewToHide.animate().alpha(0.0f);
        viewToShow.animate().alpha(1.0f).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                viewToHide.setVisibility(View.GONE);
                viewToShow.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }


    @OnClick(R.id.main_iv_setting)
    void setIvSettings() {
        pauseGame();
        setDialogSettings();
    }


    @OnClick(R.id.main_game_over_btn_start_again)
    void startAgain() {
        if (sound) mClick.start();
        doIn(mainCounter);
        current_level = 1;
        score = 0;
        questions_score = 0;
        getNewQuestion(current_level);
        performAnimation(layoutGameOver, layoutQuiz);
    }

    @OnClick(R.id.main_game_over_btn_watch_video)
    void watchVideo() {
        showRewardedVideo();
    }

    @OnClick(R.id.main_game_over_btn_to_quiz)
    void toFastQuiz() {
        getMorePoints();
    }

    @Override
    public void onResume() {
        super.onResume();
        rewardedVideoAd.resume(this);
        if (bg_sound)
            mBackground.start();
        if (gamePaused) resumeGame();
        if (!gameFinished) resumeGame();

        gamePaused = false;

    }

    @Override
    public void onPause() {
        super.onPause();
        mBackground.pause();
        rewardedVideoAd.pause(this);
        gamePaused = true;
        pauseGame();
    }

    private void showRewardedVideo() {
        if (rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.show();
        }
    }

    private void reloadInterstitialAd() {
        adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
    }

    private void getMorePoints() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    interstitialAdPrize();
                }
            });
        } else {
            interstitialAdPrize();
        }
        reloadInterstitialAd();

    }

    private void interstitialAdPrize() {
        Intent intent = new Intent(MainActivity.this, FastQuizActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("level", current_level);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Log.e("FQ " + TAG, "prize " + score + " " + current_level);
        played_quiz_count++;
        sharedPrefUtilities.setPlayedQuiz(played_quiz_count);
        startActivity(intent);
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
        ImageView mIvAudio = mDialogSettings.findViewById(R.id.dialog_setting_iv_audio);
        ImageView mIvLang = mDialogSettings.findViewById(R.id.dialog_setting_iv_language);
        ImageView mIvBgAudio = mDialogSettings.findViewById(R.id.dialog_setting_iv_bg);
        mIvAudio.getLayoutParams().height = 100;
        mIvAudio.getLayoutParams().width = 100;
        mIvLang.getLayoutParams().height = 100;
        mIvLang.getLayoutParams().width = 100;
        mIvBgAudio.getLayoutParams().height = 100;
        mIvBgAudio.getLayoutParams().width = 100;
        final Button btnSave = mDialogSettings.findViewById(R.id.dialog_setting_btn_save);
        final RadioButton rbSoundOn = mDialogSettings.findViewById(R.id.dialog_setting_rb_audio_on);
        RadioButton rbSoundOff = mDialogSettings.findViewById(R.id.dialog_setting_rb_audio_off);
        final RadioButton rbBgSoundOn = mDialogSettings.findViewById(R.id.dialog_setting_rb_bg_audio_on);
        RadioButton rbBgSoundOff = mDialogSettings.findViewById(R.id.dialog_setting_rb_bg_audio_off);
        final RadioButton rbLangEn = mDialogSettings.findViewById(R.id.dialog_setting_rb_lang_en);
        RadioButton rbLangAr = mDialogSettings.findViewById(R.id.dialog_setting_rb_lang_ar);
        language = sharedPrefUtilities.getLANGUAGE();
        if (language.equals("ar")) {
            rbLangAr.setChecked(true);
        } else {
            rbLangEn.setChecked(true);
        }
        sound = sharedPrefUtilities.getAUDIO();
        if (sound) {
            rbSoundOn.setChecked(true);
        } else {
            rbSoundOff.setChecked(true);
        }
        bg_sound = sharedPrefUtilities.getBgAudio();
        if (bg_sound) {
            rbBgSoundOn.setChecked(true);
        } else {
            rbBgSoundOff.setChecked(true);
        }
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
                if (rbBgSoundOn.isChecked() || rbBgSoundOn.isSelected()) {
                    bg_sound = true;
                } else {
                    bg_sound = false;
                }

                sharedPrefUtilities.setAudio(sound);
                sharedPrefUtilities.setBgAudio(bg_sound);

                if (!sharedPrefUtilities.getLANGUAGE().equals(language)) {
                    sharedPrefUtilities.setLanguage(language);
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                        setApplicationLanguage(language);
                    } else {
                        languageConfiguration(language);
                    }
                }
                if (sound) mClick.start();

                if (bg_sound) {
                    mBackground.start();
                } else {
                    mBackground.pause();
                }
                resumeGame();
                mDialogSettings.dismiss();
            }

        });
        mDialogSettings.show();

    }

    private void setDialogBack() {
        mDialogBack = new Dialog(this);
        mDialogBack.setContentView(R.layout.dialog_back);
        try {
            mDialogBack.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            mDialogBack.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDialogBack.setCancelable(false);
        Button btnResume = mDialogBack.findViewById(R.id.dialog_back_btn_resume);
        Button btnStart = mDialogBack.findViewById(R.id.dialog_back_btn_back_to_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sound) mClick.start();
                mDialogBack.dismiss();
                Intent i = new Intent(MainActivity.this, StartActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("audio_position", mBackground.getCurrentPosition());
                startActivity(i);
            }
        });
        btnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sound) mClick.start();
                resumeGame();
                mDialogBack.dismiss();
            }
        });

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
        if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        if (sound) mClick.start();

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

        checkAnswers(value1, value2, value3, mLastClickTime, getModel(), current_level, selected, selected_value);
    }


    private void checkAnswers(String value1, String value2, String value3, long mLastClickTime,
                              TaskModel model, final int current_level, Button selected, String selected_value) {

        if (selected_value.equals(model.getCorrect_answer())) {
            selected.setBackground(getResources().getDrawable(R.drawable.correct_answer_background));
            isCorrect = true;
            score++;
            questions_score++;
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
            currentCounter += 2000;
            doIn(currentCounter);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getNewQuestion(current_level);
            }
        }, 500);
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
        startNewLevel();

    }

    public TaskModel getModel() {
        return model;
    }

    public void setModel(TaskModel model) {
        this.model = model;
    }

    private void startNewLevel() {
        if (questions_score == 10) {
            current_level++;
            questions_score = 0;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDialogSettings != null) {
            mDialogSettings.cancel();
            mDialogSettings = null;
        }

        if (mDialogBack != null) {
            mDialogBack.cancel();
            mDialogBack = null;
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
        pauseByVideo = true;
        if (sharedPrefUtilities.getBgAudio()) {
            Log.e(TAG, "music is on");
            mBackground.pause();
        }
    }

    @Override
    public void onRewardedVideoStarted() {
        Log.e(TAG, "onRewardedVideoStarted");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Log.e(TAG, "onRewardedVideoAdClosed");
        loadRewardedVideoAd();
        if (rewardItem != null) {
//            currentCounter = 5800;
            pauseCounter = 5800;
//            doIn(5800);
            getNewQuestion(current_level);
            Toast.makeText(this, getResources().getString(R.string.get_more_points), Toast.LENGTH_SHORT).show();
            performAnimation(layoutGameOver, layoutQuiz);
        }
        if (sharedPrefUtilities.getBgAudio()) {
            mBackground.start();
        }
    }

    RewardItem rewardItem;

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Log.e(TAG, "onRewarded");
        this.rewardItem = rewardItem;

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
        startActivity(new Intent(this, SplashActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void languageConfiguration(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        startActivity(new Intent(this, SplashActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    int getHighScore(int newScore) {
        int saved = sharedPrefUtilities.getHighScore();
        if (newScore > saved) {
            isNewHighScore = true;
            sharedPrefUtilities.setHighScore(newScore);
            return newScore;
        } else {
            sharedPrefUtilities.setHighScore(score);
            isNewHighScore = false;
            return saved;
        }
    }

    @Override
    public void onBackPressed() {
        if (gameFinished) {
            super.onBackPressed();
        } else {
            if (mDialogBack != null) {
                mDialogBack.show();
            } else {
                setDialogBack();
                mDialogBack.show();
            }
            pauseGame();
        }
    }


    void pauseGame() {
        pauseCounter = currentCounter;
        Log.e(TAG, "time pause  > " + pauseCounter + " - " + currentCounter);
        mCountDownTimer.cancel();

        layoutQuiz.setVisibility(View.GONE);
        tvTimer.setVisibility(View.GONE);
    }

    private void resumeGame() {
        if (sound) mClick.start();
        Log.e(TAG, "time resume  > " + pauseCounter + " - " + currentCounter);
        layoutQuiz.setVisibility(View.VISIBLE);
        tvTimer.setVisibility(View.VISIBLE);

        getNewQuestion(current_level);
        doIn(pauseCounter);

    }


}

