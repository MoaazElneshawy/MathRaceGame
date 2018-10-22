package com.mathrace.mathrace.UI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
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
import com.mathrace.mathrace.Models.TaskModel;
import com.mathrace.mathrace.R;
import com.mathrace.mathrace.Utilities.BaseActivity;
import com.mathrace.mathrace.Utilities.MathRaceQuestionsGenerator;
import com.mathrace.mathrace.Utilities.SharedPrefUtilities;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FastQuizActivity extends BaseActivity {

    @BindView(R.id.fast_quiz_tv_score)
    TextView mTvScore;
    @BindView(R.id.fast_quiz_btn_retry)
    Button retryButton;
    @BindView(R.id.fast_quiz_btn_answer1)
    Button btnAnswer1;
    @BindView(R.id.fast_quiz_btn_answer2)
    Button btnAnswer2;
    @BindView(R.id.fast_quiz_btn_answer3)
    Button btnAnswer3;
    @BindView(R.id.fast_quiz_btn_answer4)
    Button btnAnswer4;
    @BindView(R.id.fast_quiz_tv_question)
    TextView tvQuestion;

    String TAG = "FastQuizActivity";
    boolean videoWatched = false;
    TaskModel model;
    boolean isCorrect, stop = false, sound;
    int score = 0, current_level = 3, number_of_questions = 1, played_quiz = 1;
    CountDownTimer mCountDownTimer;
    long fast_quizCounter = 20000, currentCounter = 0, moreTime;
    SharedPrefUtilities sharedPrefUtilities;

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
        sharedPrefUtilities = new SharedPrefUtilities(this);
        played_quiz = sharedPrefUtilities.getPlayedQuiz();
    }


    @OnClick(R.id.fast_quiz_btn_retry)
    void setRetryButton() {



    }


    void doIn(long time) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        mCountDownTimer = new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {
                mTvScore.setText("Timer : " + millisUntilFinished / 1000 + "\n"
                        + "Score : " + score + "\n"
                        + " Level : " + current_level);
                currentCounter = millisUntilFinished;
                btnAnswer1.setEnabled(true);
                btnAnswer2.setEnabled(true);
                btnAnswer3.setEnabled(true);
                btnAnswer4.setEnabled(true);
            }

            public void onFinish() {
                mTvScore.setText("Timer : " + 0 + "\n"
                        + "Score : " + score + "\n"
                        + " Level : " + current_level);
                Toast.makeText(FastQuizActivity.this, "Game over !", Toast.LENGTH_SHORT).show();
                btnAnswer1.setEnabled(false);
                btnAnswer2.setEnabled(false);
                btnAnswer3.setEnabled(false);
                btnAnswer4.setEnabled(false);
                stop = true;

            }
        }.start();
    }

    @OnClick({R.id.fast_quiz_btn_answer1, R.id.fast_quiz_btn_answer2, R.id.fast_quiz_btn_answer3, R.id.fast_quiz_btn_answer4})
    public void doCheck(View view) {

        String value1 = "0";
        String value2 = "1";
        String value3 = "2";
        String value4 = "3";
        String selected_value;
        Button selected;
        int id = view.getId();
        if (id == R.id.fast_quiz_btn_answer1) {
            selected_value = value1;
            selected = btnAnswer1;
        } else if (id == R.id.fast_quiz_btn_answer2) {
            selected = btnAnswer2;
            selected_value = value2;
        } else if (id == R.id.fast_quiz_btn_answer3) {
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


}
