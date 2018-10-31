package com.beansproduction.mathrace.UI;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beansproduction.mathrace.Models.TaskModel;
import com.beansproduction.mathrace.R;
import com.beansproduction.mathrace.Utilities.BaseActivity;
import com.beansproduction.mathrace.Utilities.MathRaceQuestionsGenerator;
import com.beansproduction.mathrace.Utilities.SharedPrefUtilities;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuizActivity extends BaseActivity {

    String TAG = "QuizActivity";

    MediaPlayer mClick, mBackground;
    @BindView(R.id.quiz_tv_score)
    TextView tvScore;
    @BindView(R.id.quiz_quiz_layout)
    LinearLayout layoutQuiz;
    @BindView(R.id.quiz_btn_answer1)
    Button btnAnswer1;
    @BindView(R.id.quiz_btn_answer2)
    Button btnAnswer2;
    @BindView(R.id.quiz_btn_answer3)
    Button btnAnswer3;
    @BindView(R.id.quiz_btn_answer4)
    Button btnAnswer4;
    @BindView(R.id.quiz_tv_question)
    TextView tvQuestion;
    @BindView(R.id.quiz_tv_timer)
    TextView tvTimer;
    private AdView mAdView;


    SharedPrefUtilities sharedPrefUtilities;
    Dialog mQuizDialog;
    TaskModel model;
    CountDownTimer mCountDownTimer;

    int number_of_questions = 1, current_level = 1, score = 0, saved_score, saved_level, audio_position;
    long mLastClickTime = 0, currentCounter = 0, quizCounter = 20500, pause_counter;
    boolean gamePaused = false, bg_sound, click_sound, show_hint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        ButterKnife.bind(this);
        try {
            this.getSupportActionBar().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent i = this.getIntent();
        if (i != null) {
            saved_score = i.getIntExtra("score", 10);
            saved_level = i.getIntExtra("level", 2);
            audio_position = i.getIntExtra("audio_position", 0);
        }

        MobileAds.initialize(this, getString(R.string.ads_app_id));

        setAdmob();
        sharedPrefUtilities = new SharedPrefUtilities(this);
        bg_sound = sharedPrefUtilities.getBgAudio();
        click_sound = sharedPrefUtilities.getAUDIO();
        show_hint = sharedPrefUtilities.getQuizHint();

        mClick = MediaPlayer.create(QuizActivity.this, R.raw.button_click);
        mBackground = MediaPlayer.create(QuizActivity.this, R.raw.background_audio);
        mBackground.setLooping(true);
        if (audio_position != 0) {
            mBackground.seekTo(audio_position);
        }
        if (bg_sound)
            mBackground.start();

        setQuizDialog();
        if (show_hint)
            mQuizDialog.show();
        else doIn(quizCounter);

        getNewQuestion(current_level);

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
        TextView tvHint = mQuizDialog.findViewById(R.id.dialog_quiz_tv);
        tvHint.setMovementMethod(new ScrollingMovementMethod());
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (click_sound) mClick.start();
                if (cbDontShow.isChecked()) {
                    sharedPrefUtilities.setQuizHint(false);
                }
                mQuizDialog.dismiss();
                doIn(quizCounter);
            }
        });
    }

    private void getNewQuestion(int current_level) {

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

        tvQuestion.setText(model.getQuestion());
        btnAnswer1.setText(model.getAnswer1());
        btnAnswer2.setText(model.getAnswer2());
        btnAnswer3.setText(model.getAnswer3());
        btnAnswer4.setText(model.getAnswer4());
        setModel(model);
        number_of_questions++;
        startNewLevel();
    }

    private void startNewLevel() {
        if (number_of_questions == 5) {
            current_level++;
            number_of_questions = 1;
        }
    }

    public TaskModel getModel() {
        return model;
    }

    public void setModel(TaskModel model) {
        this.model = model;
    }

    private void checkAnswers(String value1, String value2, String value3, String value4,
                              TaskModel model, final int current_level, Button selected, String selected_value) {
        if (selected_value.equals(model.getCorrect_answer())) {
            selected.setBackground(getResources().getDrawable(R.drawable.correct_answer_background));
            score++;
        } else {
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getNewQuestion(current_level);
            }
        }, 500);

    }


    @OnClick({R.id.quiz_btn_answer1, R.id.quiz_btn_answer2, R.id.quiz_btn_answer3, R.id.quiz_btn_answer4})
    public void doCheck(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        if (click_sound) mClick.start();

        String value1 = "0";
        String value2 = "1";
        String value3 = "2";
        String value4 = "3";
        String selected_value;
        Button selected;
        int id = view.getId();
        if (id == R.id.quiz_btn_answer1) {
            selected_value = value1;
            selected = btnAnswer1;
        } else if (id == R.id.quiz_btn_answer2) {
            selected = btnAnswer2;
            selected_value = value2;
        } else if (id == R.id.quiz_btn_answer3) {
            selected_value = value3;
            selected = btnAnswer3;
        } else {
            selected = btnAnswer4;
            selected_value = value4;
        }
        checkAnswers(value1, value2, value3, value4, getModel(), current_level, selected, selected_value);
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
                tvTimer.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                tvTimer.setText("0");
                tvScore.setText(getResources().getString(R.string.score) + " : " + score +
                        "\n" + getResources().getString(R.string.level) + " : " + current_level);

                if (layoutQuiz.getVisibility() == View.VISIBLE) {
                    Log.e("FQ in Quiz" + TAG, saved_score + " - " + score + " - " + saved_level);
                    goToMain(score, saved_score, saved_level);
                }
            }

        }.start();

    }

    private void goToMain(int score, int saved_score, int saved_level) {
        Intent back = new Intent(QuizActivity.this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra("score", saved_score)
                .putExtra("audio_position", mBackground.getCurrentPosition())
                .putExtra("time", score * 1000l)
                .putExtra("level", saved_level);
        startActivity(back);

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, getString(R.string.quiz_back), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gamePaused) resumeGame();
        if (bg_sound && !mBackground.isPlaying()) mBackground.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBackground.pause();
        gamePaused = true;
        pauseGame();
    }

    void pauseGame() {
        pause_counter = currentCounter;
        Log.e(TAG, " timer > pause >> " + pause_counter + " - " + currentCounter);
        layoutQuiz.setVisibility(View.GONE);
        tvTimer.setVisibility(View.GONE);
        getNewQuestion(current_level);
    }

    void resumeGame() {
        gamePaused = false;
        layoutQuiz.setVisibility(View.VISIBLE);
        tvTimer.setVisibility(View.VISIBLE);
        doIn(pause_counter);
        Log.e(TAG, " timer > resume >> " + pause_counter + " - " + currentCounter);
    }
}
