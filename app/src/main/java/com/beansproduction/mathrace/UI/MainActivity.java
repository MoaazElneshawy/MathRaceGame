package com.beansproduction.mathrace.UI;

import android.animation.Animator;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
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
import com.beansproduction.mathrace.Models.TaskModel;
import com.beansproduction.mathrace.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.beansproduction.mathrace.Utilities.BaseActivity;
import com.beansproduction.mathrace.Utilities.MathRaceQuestionsGenerator;
import com.beansproduction.mathrace.Utilities.SharedPrefUtilities;

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
    NestedScrollView layoutGameOver;
    @BindView(R.id.main_game_over_btn_watch_video)
    Button btnWatchVideo;
    @BindView(R.id.main_game_over_btn_to_quiz)
    Button btnPlayQuiz;
    @BindView(R.id.main_game_over_btn_start_again)
    Button btnStartAgain;
    @BindView(R.id.main_game_over_tv_score)
    TextView tvGameOverScore;
    @BindView(R.id.main_game_over_tv_quiz_hint)
    TextView tvQuizHint;
    @BindView(R.id.main_game_over_tv_video_hint)
    TextView tvVideoHint;
    @BindView(R.id.main_game_over_iv_status)
    ImageView ivPlayerStatus;
    //    @BindView(R.id.main_screenshot_fbtn)
//    FloatingActionButton fbtnScreenshot;
    @BindView(R.id.share_pic_layout)
    LinearLayout sharePicLayout;
    @BindView(R.id.share_text_layout)
    LinearLayout shareTextLayout;

    CallbackManager callbackManager;
    ShareDialog shareDialog;
    CountDownTimer mCountDownTimer;
    long mainCounter = 15500, currentCounter = 0, pauseCounter = 0, mLastClickTime = 0;
    int current_level = 1, questions_score = 0, watched_video_count = 0, played_quiz_count = 0, score = 0, bg_audio_position = 0;
    public static final int REQUEST_CODE = 10;
    Dialog mDialogSettings, mDialogBack, mDialogStart;
    MediaPlayer mClick, mBackground;
    private AdView mAdView;
    private InterstitialAd interstitialAd;
    private RewardedVideoAd rewardedVideoAd;
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
    AdRequest adRequest;
    TaskModel model;
    public static final String TAG = "MainActivity";
    boolean isCorrect, sound, bg_sound, isNewHighScore, gameFinished = true, gamePaused = false, pauseByVideo = false, hasNetwork, isCustom, isFromQuiz = false;
    SharedPrefUtilities sharedPrefUtilities;
    String language;
    int op_add = 0, op_minus = 0, op_multiply = 0, op_division = 0, saved_custom_level = 0, saved_custom_time = R.id.dialog_start_rb_time_thirty;
    List<Integer> levels;

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
        MobileAds.initialize(this, getString(R.string.ads_app_id));
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

        }
        setAdmob();

        if (i.hasExtra("score")) {
            isFromQuiz = true;
            int saved_score = i.getIntExtra("score", 0);
            long time = i.getLongExtra("time", 0l);
            int saved_level = i.getIntExtra("level", 0);
            bg_audio_position = i.getIntExtra("audio_position", 0);
            // Log.e("FQ in Main " + TAG, "intent " + saved_score + " - " + time + " " + saved_level);
            if (time > 0) {
                doIn(time);
                score = saved_score;
                current_level = saved_level;
                layoutQuiz.setVisibility(View.VISIBLE);
                getNewQuestion(saved_level);
                Toast.makeText(this, getString(R.string.quiz_sucess), Toast.LENGTH_LONG).show();
            } else {
                layoutQuiz.setVisibility(View.GONE);
//                fbtnScreenshot.setVisibility(View.VISIBLE);
                layoutGameOver.setVisibility(View.VISIBLE);
                tvGameOverScore.setText(R.string.quiz_failed);
                ivPlayerStatus.setImageResource(R.drawable.ic_loser);
            }
        } else {
            setStartDialog(isFromQuiz);
//            doIn(mainCounter);
//            getNewQuestion(current_level);
            layoutQuiz.setVisibility(View.VISIBLE);
        }

        if (bg_audio_position != 0) {
            mBackground.seekTo(bg_audio_position);
        }

        if (sharedPrefUtilities.getBgAudio()) {
            mBackground.start();
        }

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_id));
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
//                fbtnScreenshot.setVisibility(View.GONE);
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
//                fbtnScreenshot.setVisibility(View.VISIBLE);
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

                if (!isCustom) {
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
                    } else {
                        btnPlayQuiz.setVisibility(View.VISIBLE);
                        tvQuizHint.setVisibility(View.VISIBLE);
                    }

                } else {
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
//                Log.e(TAG, "Facebook registration success");
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
                // Log.e(TAG, e.getMessage());
            }
        });

    }

    @OnClick({R.id.share_text_layout, R.id.share_text_tv, R.id.share_text_iv})
    void shareOnFB() {
        if (hasNetwork) {
            if (ShareDialog.canShow(ShareLinkContent.class)) {

                ShareContent content = new ShareLinkContent.Builder()

                        .setQuote((getString(R.string.score) + " " + score + " " + getString(R.string.share_text)))
                        .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.beansproduction.mathrace"))

                        .setShareHashtag(new ShareHashtag.Builder()
                                .setHashtag("#MathRace")
                                .build())

                        .build();

                shareDialog.show(content);
                Toast.makeText(this, getString(R.string.in_progress), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
            isNetworkAvailable();
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
                if (layoutGameOver.getVisibility() == View.VISIBLE) {
                    mAdView.setVisibility(View.GONE);
                } else {
                    mAdView.setVisibility(View.VISIBLE);
                }
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
        if (bg_sound && !mBackground.isPlaying()) mBackground.start();

        setStartDialog(false);
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
        isNetworkAvailable();
        rewardedVideoAd.resume(this);
        if (bg_sound && !mBackground.isPlaying())
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
        Intent intent = new Intent(MainActivity.this, QuizActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("level", current_level);
        intent.putExtra("audio_position", mBackground.getCurrentPosition());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Log.e("FQ " + TAG, "prize " + score + " " + current_level);
        played_quiz_count++;
        sharedPrefUtilities.setPlayedQuiz(played_quiz_count);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_out, R.anim.slide_in);

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

    private void setStartDialog(boolean isFromQuiz) {
        mAdView.setVisibility(View.VISIBLE);
        score = 0;
        current_level = 1;
        levels = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            levels.add(i, (i + 1));
        }
        mDialogStart = new Dialog(this);
        mDialogStart.setContentView(R.layout.dialog_start);
        try {
            mDialogStart.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            mDialogStart.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDialogStart.setCancelable(false);
        RadioGroup rgMode = mDialogStart.findViewById(R.id.dialog_start_rg_mode);
        final RadioButton rbTimeThirty = mDialogStart.findViewById(R.id.dialog_start_rb_time_thirty);
        final RadioButton rbTimeSixty = mDialogStart.findViewById(R.id.dialog_start_rb_time_sixty);
        final RadioButton rbTimeTwelve = mDialogStart.findViewById(R.id.dialog_start_rb_time_twelve);
        final RadioButton rbNormal = mDialogStart.findViewById(R.id.dialog_start_rb_normal_mode);
        final RadioButton rbCustom = mDialogStart.findViewById(R.id.dialog_start_rb_custome_mode);
        final CheckBox cbAdd = mDialogStart.findViewById(R.id.dialog_start_cb_add);
        final CheckBox cbMinus = mDialogStart.findViewById(R.id.dialog_start_cb_minus);
        final CheckBox cbMultiply = mDialogStart.findViewById(R.id.dialog_start_cb_multiply);
        final CheckBox cbDivision = mDialogStart.findViewById(R.id.dialog_start_cb_division);
        final ImageView ivAdd = mDialogStart.findViewById(R.id.dialog_start_iv_add);
        final ImageView ivMinus = mDialogStart.findViewById(R.id.dialog_start_iv_minus);
        final ImageView ivMultiply = mDialogStart.findViewById(R.id.dialog_start_iv_multiply);
        final ImageView ivDivision = mDialogStart.findViewById(R.id.dialog_start_iv_division);
        final LinearLayout operationsLayout = mDialogStart.findViewById(R.id.dialog_start_operations_layout);
        final Spinner levelsSpinner = mDialogStart.findViewById(R.id.dialog_start_spinner_level);
        ArrayAdapter spinnerAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, levels);

        levelsSpinner.setAdapter(spinnerAdapter);
        Button btnStart = mDialogStart.findViewById(R.id.dialog_start_btn);

        switch (saved_custom_time) {
            case R.id.dialog_start_rb_time_thirty:
                rbTimeThirty.setChecked(true);
                break;
            case R.id.dialog_start_rb_time_sixty:
                rbTimeSixty.setChecked(true);
                break;
            case R.id.dialog_start_rb_time_twelve:
                rbTimeTwelve.setChecked(true);
                break;
            default:
                rbTimeThirty.setChecked(true);
                break;
        }

        if (!isFromQuiz)
            mDialogStart.show();

        if (isCustom) rbCustom.setChecked(true);
        else rbNormal.setChecked(true);

        if (op_add == 1) cbAdd.setChecked(true);
        if (op_minus == 1) cbMinus.setChecked(true);
        if (op_multiply == 1) cbMultiply.setChecked(true);
        if (op_division == 1) cbDivision.setChecked(true);
        levelsSpinner.setSelection(saved_custom_level);

        if (rbNormal.isChecked()) {
            cbAdd.setEnabled(false);
            cbMinus.setEnabled(false);
            cbMultiply.setEnabled(false);
            cbDivision.setEnabled(false);
            ivAdd.setEnabled(false);
            ivMultiply.setEnabled(false);
            ivMinus.setEnabled(false);
            ivDivision.setEnabled(false);
            rbTimeThirty.setEnabled(false);
            rbTimeSixty.setEnabled(false);
            rbTimeTwelve.setEnabled(false);
            levelsSpinner.setEnabled(false);
        } else {
            cbAdd.setEnabled(true);
            cbMinus.setEnabled(true);
            cbMultiply.setEnabled(true);
            cbDivision.setEnabled(true);
            ivAdd.setEnabled(true);
            ivMultiply.setEnabled(true);
            ivMinus.setEnabled(true);
            ivDivision.setEnabled(true);
            rbTimeThirty.setEnabled(true);
            rbTimeSixty.setEnabled(true);
            rbTimeTwelve.setEnabled(true);
            levelsSpinner.setEnabled(true);
        }

        rgMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.dialog_start_rb_normal_mode) {
                    cbAdd.setEnabled(false);
                    cbMinus.setEnabled(false);
                    cbMultiply.setEnabled(false);
                    cbDivision.setEnabled(false);
                    ivAdd.setEnabled(false);
                    ivMultiply.setEnabled(false);
                    ivMinus.setEnabled(false);
                    ivDivision.setEnabled(false);
                    rbTimeThirty.setEnabled(false);
                    rbTimeSixty.setEnabled(false);
                    rbTimeTwelve.setEnabled(false);
                    levelsSpinner.setEnabled(false);
                } else {
                    cbAdd.setEnabled(true);
                    levelsSpinner.setEnabled(true);
                    cbMinus.setEnabled(true);
                    cbMultiply.setEnabled(true);
                    cbDivision.setEnabled(true);
                    ivAdd.setEnabled(true);
                    ivMultiply.setEnabled(true);
                    ivMinus.setEnabled(true);
                    ivDivision.setEnabled(true);
                    rbTimeThirty.setEnabled(true);
                    rbTimeSixty.setEnabled(true);
                    rbTimeTwelve.setEnabled(true);
                }
            }
        });


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rbCustom.isSelected() || rbCustom.isChecked()) {
                    isCustom = true;
                } else {
                    isCustom = false;
                }
                if (rbNormal.isSelected() || rbNormal.isChecked()) {
                    isCustom = false;
                } else {
                    isCustom = true;
                }

                if (rbCustom.isChecked() || rbCustom.isSelected()) {
                    if (rbTimeThirty.isSelected() || rbTimeThirty.isChecked()) {
                        mainCounter = 30500;
//                        mainCounter = 3500;
                        saved_custom_time = rbTimeThirty.getId();
                    } else if (rbTimeSixty.isSelected() || rbTimeSixty.isChecked()) {
                        mainCounter = 60500;
//                        mainCounter = 6500;
                        saved_custom_time = rbTimeSixty.getId();
                    } else if (rbTimeTwelve.isChecked() || rbTimeTwelve.isSelected()) {
                        mainCounter = 120500;
//                        mainCounter = 12500;
                        saved_custom_time = rbTimeTwelve.getId();
                    } else {
                        mainCounter = 15500;
//                        mainCounter = 5500;
                    }

                    if (cbAdd.isChecked() || cbAdd.isSelected()) {
                        op_add = 1;
                    } else {
                        op_add = 0;
                    }

                    if (cbMinus.isChecked() || cbMinus.isSelected()) {
                        op_minus = 1;
                    } else {
                        op_minus = 0;
                    }

                    if (cbDivision.isChecked() || cbDivision.isSelected()) {
                        op_division = 1;
                    } else {
                        op_division = 0;
                    }

                    if (cbMultiply.isChecked() || cbMultiply.isSelected()) {
                        op_multiply = 1;
                    } else {
                        op_multiply = 0;
                    }
                    saved_custom_level = levelsSpinner.getSelectedItemPosition();
                }

                if (!isCustom) {
                    mDialogStart.dismiss();
                    mainCounter = 15500;
                    current_level = 1;
                    getNewQuestion(current_level);
                    doIn(mainCounter);
                } else {
                    if (op_add == 0 && op_minus == 0 && op_multiply == 0 && op_division == 0) {
                        operationsLayout.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake));
                        Toast.makeText(MainActivity.this, getString(R.string.custom_no_selection), Toast.LENGTH_SHORT).show();
                    } else {
                        mDialogStart.dismiss();
                        current_level = levelsSpinner.getSelectedItemPosition() + 1;
                        getNewCustomQuestion(op_add, op_minus, op_multiply, op_division, current_level);
                        doIn(mainCounter);
                    }
                }
            }
        });
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
        adView.setAdUnitId(getString(R.string.banner_id));
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
            if (isCustom) {
                currentCounter += 2300;
            } else {
                currentCounter += 1300;
            }
            doIn(currentCounter);
        }
        startNewLevel();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isCustom)
                    getNewCustomQuestion(op_add, op_minus, op_multiply, op_division, current_level);
                else
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

    }

    private void getNewCustomQuestion(int op_add, int op_minus, int op_multiply, int op_division, int current_level) {
//        Log.e(TAG, "Custom Question > " + op_add + " - " + op_minus + " - " + op_multiply + " - " + op_division + " - " + current_level);

        btnAnswer1.setBackground(getResources().getDrawable(R.drawable.answers_background));
        btnAnswer2.setBackground(getResources().getDrawable(R.drawable.answers_background));
        btnAnswer3.setBackground(getResources().getDrawable(R.drawable.answers_background));
        btnAnswer4.setBackground(getResources().getDrawable(R.drawable.answers_background));

        List<String> list = MathRaceQuestionsGenerator.getCustomQuestion(op_add, op_minus, op_multiply, op_division, current_level);
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
        if (mDialogSettings != null) {
            mDialogSettings.cancel();
            mDialogSettings = null;
        }

        if (mDialogBack != null) {
            mDialogBack.cancel();
            mDialogBack = null;
        }
        if (mDialogStart != null) {
            mDialogStart.cancel();
            mDialogStart = null;
        }
        super.onStop();
    }

    private void loadRewardedVideoAd() {
        if (!rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.loadAd(getString(R.string.rewarded_id), new AdRequest.Builder().build());
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {
//        // Log.e(TAG, "onRewardedVideoAdLoaded");

    }

    @Override
    public void onRewardedVideoAdOpened() {
//        // Log.e(TAG, "onRewardedVideoAdOpened");
        watched_video_count++;
        sharedPrefUtilities.setWatchedVideo(watched_video_count);
        loadRewardedVideoAd();
        pauseByVideo = true;
        if (sharedPrefUtilities.getBgAudio()) {
            // Log.e(TAG, "music is on");
            mBackground.pause();
        }
    }

    @Override
    public void onRewardedVideoStarted() {
        // Log.e(TAG, "onRewardedVideoStarted");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        // Log.e(TAG, "onRewardedVideoAdClosed");
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
        // Log.e(TAG, "onRewarded");
        this.rewardItem = rewardItem;

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        // Log.e(TAG, "onRewardedVideoAdLeftApplication");
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        // Log.e(TAG, "onRewardedVideoAdFailedToLoad");
    }

    @Override
    public void onRewardedVideoCompleted() {
        // Log.e(TAG, "onRewardedVideoCompleted");
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
        if (newScore >= saved) {
            isNewHighScore = true;
            sharedPrefUtilities.setHighScore(newScore);
            return newScore;
        } else {
            sharedPrefUtilities.setHighScore(saved);
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

    @Override
    public void onDestroy() {
        rewardedVideoAd.destroy(this);
        super.onDestroy();
    }


    void pauseGame() {
        pauseCounter = currentCounter;
        // Log.e(TAG, "time pause  > " + pauseCounter + " - " + currentCounter);
        try {
            mCountDownTimer.cancel();
        } catch (Exception e) {

        }
        layoutQuiz.setVisibility(View.GONE);
        tvTimer.setVisibility(View.GONE);
    }

    private void resumeGame() {
        if (sound) mClick.start();

        // Log.ef(TAG, "time resume  > " + pauseCounter + " - " + currentCounter);
        layoutQuiz.setVisibility(View.VISIBLE);
        tvTimer.setVisibility(View.VISIBLE);

        if (isCustom)
            getNewCustomQuestion(op_add, op_minus, op_multiply, op_division, current_level);
        else
            getNewQuestion(current_level);

        doIn(pauseCounter);

    }


    private void isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
                //  connected
                hasNetwork = true;
            } else {
                // code if not connected
                hasNetwork = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.share_pic_layout, R.id.share_pic_iv, R.id.share_pic_tv})
    void performTakeScreenshot() {
        takeScreenshot();
    }

    private void takeScreenshot() {
        try {
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            shareScreenshotOnFB(bitmap);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void shareScreenshotOnFB(Bitmap bitmap) {
        SharePhoto.Builder builder = new SharePhoto.Builder();
        builder.setBitmap(bitmap);
        builder.setCaption("I am the Boss !");
        SharePhoto photo = builder.build();
        try {
            if (hasNetwork) {
                if (ShareDialog.canShow(SharePhotoContent.class)) {
                    SharePhotoContent cc = new SharePhotoContent.Builder()
                            .addPhoto(photo)
                            .build();
                    shareDialog.show(cc);
                    Toast.makeText(this, getString(R.string.in_progress), Toast.LENGTH_SHORT).show();

                } else {
                    mAdView.setVisibility(View.VISIBLE);
                    Toast.makeText(this, getString(R.string.share_image_error), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                isNetworkAvailable();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

