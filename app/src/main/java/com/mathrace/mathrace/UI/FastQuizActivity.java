package com.mathrace.mathrace.UI;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
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
    TextView tvScore;
    @BindView(R.id.fast_quiz_quiz_layout)
    LinearLayout layoutQuiz;
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
    @BindView(R.id.fast_quiz_tv_timer)
    TextView tvTimer;

    private AdView mAdView;
    String TAG = "FastQuizActivity";
    TaskModel model;
    boolean isCorrect, pause = false, sound, bg_sound;
    int score = 0, current_level = 1, number_of_questions = 1, saved_score, saved_level;
    CountDownTimer mCountDownTimer;
    long fast_quizCounter = 20800, currentCounter = 0, mLastClickTime = 0, pauseCounter = 0;
    SharedPrefUtilities sharedPrefUtilities;
    Dialog mQuizDialog;
    MediaPlayer mClick, mBackground;

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
        setAdmob();
        Intent i = this.getIntent();
        if (i != null) {
            saved_score = i.getIntExtra("score", 10);
            saved_level = i.getIntExtra("level", 2);
        }
        Log.e("FQ " + TAG, "OnCreate " + saved_score + " - " + saved_level);
        sharedPrefUtilities = new SharedPrefUtilities(this);
        mClick = MediaPlayer.create(FastQuizActivity.this, R.raw.button_click);
        mBackground = MediaPlayer.create(this, R.raw.background_audio);
        mBackground.setLooping(true);
        bg_sound = sharedPrefUtilities.getBgAudio();
        sound = sharedPrefUtilities.getAUDIO();
        setQuizDialog();
        getNewQuestion(current_level);
        if (sharedPrefUtilities.getQuizHint()) {
            mQuizDialog.show();
            Log.e("FQ " + TAG, " - dialogShow");
        } else {
            doIn(fast_quizCounter);
            Log.e("FQ " + TAG, " - start 3 6ol");
        }
    }

    private void setQuizDialog() {
        mQuizDialog = new Dialog(this);
        mQuizDialog.setContentView(R.layout.dialog_quiz_hint);
        try {
            mQuizDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            mQuizDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mQuizDialog.setCancelable(false);
        final CheckBox cbDontShow = mQuizDialog.findViewById(R.id.dialog_quiz_cb);
        Button btnSave = mQuizDialog.findViewById(R.id.dialog_quiz_btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbDontShow.isChecked()) {
                    sharedPrefUtilities.setQuizHint(false);
                }
                mQuizDialog.dismiss();
                doIn(fast_quizCounter);
            }
        });
    }

    void doIn(long time) {
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
//                ivSettings.setVisibility(View.INVISIBLE);
            }

            public void onFinish() {
                tvTimer.setText("0");
                tvScore.setText(getResources().getString(R.string.score) + " : " + score +
                        "\n" + getResources().getString(R.string.level) + " : " + current_level);

                btnAnswer1.setEnabled(false);
                btnAnswer2.setEnabled(false);
                btnAnswer3.setEnabled(false);
                btnAnswer4.setEnabled(false);
                Intent back = new Intent(FastQuizActivity.this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .putExtra("score", saved_score)
                        .putExtra("time", score * 1000l)
                        .putExtra("level", saved_level);
                Log.e("FQ " + TAG, saved_score + " - " + score + " - " + saved_level);
                Toast.makeText(FastQuizActivity.this, getString(R.string.quiz_sucess), Toast.LENGTH_LONG).show();
                startActivity(back);
                finish();

            }

        }.start();

    }

    @OnClick({R.id.fast_quiz_btn_answer1, R.id.fast_quiz_btn_answer2, R.id.fast_quiz_btn_answer3, R.id.fast_quiz_btn_answer4})
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
        btnAnswer1.setEnabled(false);
        btnAnswer2.setEnabled(false);
        btnAnswer3.setEnabled(false);
        btnAnswer4.setEnabled(false);
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
            score--;
        }
//        if (isCorrect) {
//            currentCounter += 1000;
//            doIn(currentCounter);
//        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getNewQuestion(current_level);
            }
        }, 500);

    }

    private void getNewQuestion(int current_level) {
        btnAnswer1.setEnabled(true);
        btnAnswer2.setEnabled(true);
        btnAnswer3.setEnabled(true);
        btnAnswer4.setEnabled(true);
        btnAnswer1.setBackground(getResources().getDrawable(R.drawable.answers_background));
        btnAnswer2.setBackground(getResources().getDrawable(R.drawable.answers_background));
        btnAnswer3.setBackground(getResources().getDrawable(R.drawable.answers_background));
        btnAnswer4.setBackground(getResources().getDrawable(R.drawable.answers_background));

        List<String> list = MathRaceQuestionsGenerator.getQuizQuestion(current_level);
        TaskModel model = new TaskModel();
        model.setQuestion(list.get(0) + "  " + list.get(1) + "  " + list.get(2) + " " + list.get(3) + " " + list.get(4));
        model.setAnswer1(list.get(5));
        model.setAnswer2(list.get(6));
        model.setAnswer3(list.get(7));
        model.setAnswer4(list.get(8));
        model.setCorrect_answer(list.get(9));
        Log.e("Generator", "Quiz >  " + MathRaceQuestionsGenerator.getQuizQuestion(1));

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
        if (number_of_questions == 5) {
            current_level++;
            number_of_questions = 1;
        }
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

    @Override
    protected void onPause() {
        super.onPause();
        if (bg_sound)
            mBackground.pause();
        pauseGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bg_sound) {
            mBackground.start();
        }
        if (pause) resumeGame();
        pause = false;
    }

    void pauseGame() {
        pauseCounter = currentCounter;
        Log.e(TAG, "time pause  > " + pauseCounter + " - " + currentCounter);
        mCountDownTimer.cancel();
        pause = true;
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

    @Override
    public void onBackPressed() {

    }
}
