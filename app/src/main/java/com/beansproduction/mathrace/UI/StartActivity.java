package com.beansproduction.mathrace.UI;

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
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.beansproduction.mathrace.R;
import com.beansproduction.mathrace.Utilities.BaseActivity;
import com.beansproduction.mathrace.Utilities.SharedPrefUtilities;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartActivity extends BaseActivity {

    String TAG = "StartActivity";
    @BindView(R.id.splash_btn_start_game)
    Button btnStart;
    @BindView(R.id.splash_btn_about)
    Button btnAbout;

    private InterstitialAd interstitialAd;
    AdRequest adRequest;
    boolean sound, bg_sound;
    Dialog mDialogAbout, mDialogSettings;
    MediaPlayer mClick, mBackground;
    SharedPrefUtilities sharedPrefUtilities;
    int bg_audio_position = 0;
    String language;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        try {
            this.getSupportActionBar().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
        MobileAds.initialize(this, getString(R.string.ads_app_id));
        setAdmob();
        sharedPrefUtilities = new SharedPrefUtilities(this);
        mClick = MediaPlayer.create(StartActivity.this, R.raw.button_click);
        mBackground = MediaPlayer.create(this, R.raw.background_audio);
        mBackground.setLooping(true);
        setDialogAbout();
        sharedPrefUtilities.setWatchedVideo(0);
        sharedPrefUtilities.setPlayedQuiz(0);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_id));
        adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
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

//        try {
//            Log.e(TAG, "token > " + FirebaseInstanceId.getInstance().getToken());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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

    @Override
    protected void onPause() {
        super.onPause();
        // Log.e(TAG, "onPause");

        if (sharedPrefUtilities.getBgAudio())
            mBackground.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Log.e(TAG, "OnResume");
        if (sharedPrefUtilities.getBgAudio() && !mBackground.isPlaying())
            mBackground.start();

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

                mDialogSettings.dismiss();
            }

        });

    }

    @OnClick(R.id.splash_btn_settings)
    void setSettings() {
        if (mDialogSettings != null) {
            mDialogSettings.show();
        } else {
            setDialogSettings();
            mDialogSettings.show();
        }
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
//        startActivity(new Intent(this, SplashActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        recreate();
    }

    private void languageConfiguration(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
//        startActivity(new Intent(this, SplashActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        recreate();
    }

    @OnClick(R.id.splash_btn_start_game)
    void toStartGame() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        if (sharedPrefUtilities.getAUDIO()) {
            mClick.start();
        }
        goToGame();

    }

    long mLastClickTime = 0;

    private void goToGame() {

        mLastClickTime = SystemClock.elapsedRealtime();
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

    private void reloadInterstitialAd() {
        adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
    }

    private void interstitialAdPrize() {

        startActivity(new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra("audio_position", mBackground.getCurrentPosition()));
        mBackground.pause();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);


    }


    @OnClick(R.id.splash_btn_about)
    void toAbout() {
        if (sharedPrefUtilities.getAUDIO()) {
            mClick.start();
        }
        if (mDialogAbout == null) {
            setDialogAbout();
            mDialogAbout.show();
        } else mDialogAbout.show();
    }

    @Override
    public void onBackPressed() {
        mBackground.pause();
        finish();
    }

    private void setDialogAbout() {
        mDialogAbout = new Dialog(this);
        mDialogAbout.setContentView(R.layout.dialog_about);
        try {
            mDialogAbout.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            mDialogAbout.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDialogAbout.setCancelable(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDialogAbout != null) {
            mDialogAbout.cancel();
            mDialogAbout = null;
        }
    }

}
