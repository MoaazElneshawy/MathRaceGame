package com.beansproduction.mathrace.UI;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.beansproduction.mathrace.R;
import com.beansproduction.mathrace.Utilities.BaseActivity;
import com.beansproduction.mathrace.Utilities.SharedPrefUtilities;

import java.security.MessageDigest;
import java.util.Locale;

import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity {


    SharedPrefUtilities sharedPrefUtilities;
    String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        try {
            this.getSupportActionBar().hide();
            getWindow().
                    getDecorView().
                    setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        try {
            manager.cancelAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sharedPrefUtilities = new SharedPrefUtilities(this);
        sharedPrefUtilities.setWatchedVideo(0);
        sharedPrefUtilities.setPlayedQuiz(0);
        language = sharedPrefUtilities.getLANGUAGE();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setApplicationLanguage(language);
        } else {
            languageConfiguration(language);
        }

//        getSHAKey();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, StartActivity.class));
                overridePendingTransition(R.anim.slide_out, R.anim.slide_in);

            }
        }, 3000);
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
    }

    private void languageConfiguration(String language) {

        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        sharedPrefUtilities.setLanguage(language);
    }

    void getSHAKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.beansproduction.mathrace", PackageManager.GET_SIGNATURES);
            for (Signature s : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(s.toByteArray());
                // Log.e("keyHash",Base64.encodeToString(md.digest(),Base64.DEFAULT));

            }
        } catch (Exception e) {
            e.printStackTrace();
            // Log.e("keyHash"," -e --  "+e.getMessage());

        }
    }
}
