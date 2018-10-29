package com.mathrace.mathrace.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefUtilities {
    SharedPreferences sharedPreferences;

    public SharedPrefUtilities(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static final String AUDIO = "audio";
    private static final String BG_AUDIO = "background_audio";
    private static final String LANGUAGE = "language";
    private static final String HIGH_SCORE = "high_score";
    private static final String PLAYED_QUIZ = "played_quiz";
    private static final String WATCHED_VIDEO = "watched_video";
    private static final String QUIZ_HINT = "quiz_hint";

    public void setQuizHint(boolean show) {
        sharedPreferences.edit().putBoolean(QUIZ_HINT, show).apply();
    }

    public boolean getQuizHint() {
        return sharedPreferences.getBoolean(QUIZ_HINT, true);
    }

    public void setAudio(boolean audio) {
        sharedPreferences.edit().putBoolean(AUDIO, audio).apply();
    }

    public boolean getAUDIO() {
        return sharedPreferences.getBoolean(AUDIO, true);
    }

    public void setBgAudio(boolean audio) {
        sharedPreferences.edit().putBoolean(BG_AUDIO, audio).apply();
    }

    public boolean getBgAudio() {
        return sharedPreferences.getBoolean(BG_AUDIO, true);
    }

    public void setLanguage(String language) {
        sharedPreferences.edit().putString(LANGUAGE, language).apply();
    }

    public String getLANGUAGE() {
        return sharedPreferences.getString(LANGUAGE, "en");
    }

    public void setHighScore(int score) {
        sharedPreferences.edit().putInt(HIGH_SCORE, score).apply();
    }

    public int getHighScore() {
        return sharedPreferences.getInt(HIGH_SCORE, 0);
    }

    public void setPlayedQuiz(int playedQuiz) {
        sharedPreferences.edit().putInt(PLAYED_QUIZ, playedQuiz).apply();
    }

    public int getPlayedQuiz() {
        return sharedPreferences.getInt(PLAYED_QUIZ, 0);
    }

    public void setWatchedVideo(int watchedVideo) {
        sharedPreferences.edit().putInt(WATCHED_VIDEO, watchedVideo).apply();
    }

    public int getWatchedVideo() {
        return sharedPreferences.getInt(WATCHED_VIDEO, 0);
    }
}
