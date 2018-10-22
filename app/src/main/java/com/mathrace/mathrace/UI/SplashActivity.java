package com.mathrace.mathrace.UI;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Space;

import com.mathrace.mathrace.R;
import com.mathrace.mathrace.Utilities.BaseActivity;
import com.mathrace.mathrace.Utilities.SharedPrefUtilities;

import java.util.Locale;

public class SplashActivity extends BaseActivity {
    SharedPrefUtilities sharedPrefUtilities;
    String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        try {
            this.getSupportActionBar().hide();
            getWindow().
                    getDecorView().
                    setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        }, 2000);
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

}
