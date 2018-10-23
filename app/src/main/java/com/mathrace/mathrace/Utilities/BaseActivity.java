package com.mathrace.mathrace.Utilities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mathrace.mathrace.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BaseActivity extends AppCompatActivity {
    SharedPrefUtilities prefUtilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefUtilities = new SharedPrefUtilities(this);
        applyAppFont();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     * This method to apply custom font in activity using third-party library.
     */
    private void applyAppFont() {
        if (prefUtilities.getLANGUAGE().equals("ar")) {
            CalligraphyConfig.initDefault(
                    new CalligraphyConfig.Builder().setDefaultFontPath("fonts/Lalezar-Regular.ttf")
                            .setFontAttrId(R.attr.fontPath)
                            .build());
        }else {
            CalligraphyConfig.initDefault(
                    new CalligraphyConfig.Builder().setDefaultFontPath("fonts/BubblegumSans-Regular.ttf")
                            .setFontAttrId(R.attr.fontPath)
                            .build());
        }
    }
}
