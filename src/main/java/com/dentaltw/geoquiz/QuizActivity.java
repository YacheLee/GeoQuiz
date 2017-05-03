package com.dentaltw.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT= 0;

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mPrevButton;
    private ImageButton mNextButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;
    private TextView mApiTextView;

    private int mCurrentIndex = 0;
    private boolean mIsCheater;

    private Boolean[] answers = {null, null, null, null, null, null};
    private Question[] mQuestionBank = new Question[]{
        new Question(R.string.question_australia, true),
        new Question(R.string.question_oceans, true),
        new Question(R.string.question_mideast, false),
        new Question(R.string.question_africa, false),
        new Question(R.string.question_americas, true),
        new Question(R.string.question_asia, true),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if(savedInstanceState!=null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }

        setTitle(R.string.app_name);
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);
        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mApiTextView = (TextView) findViewById(R.id.api_text_view);

        mApiTextView.setText("API Level:"+String.valueOf(Build.VERSION.SDK_INT));


        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(true);
            }
        });
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(false);
            }
        });
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            prev();
            }
        });
        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });
        updateQuestion();
    }

    @Override
    protected void onStart() {
        Log.d(TAG,"onStart() called");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG,"onResume() called");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG,"onPause() called");
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
    }

    @Override
    protected void onStop() {
        Log.d(TAG,"onStop() called");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy() called");
        super.onDestroy();
    }

    private void updateQuestion() {
        boolean isAnswered = answers[mCurrentIndex]!=null;
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        if(isAnswered){
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
        }
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        boolean isCorrect = userPressedTrue == answerIsTrue;
        answers[mCurrentIndex] = isCorrect;
        next();
        if(hasAllAnswered()){
            if(mIsCheater){
                Toast.makeText(this, R.string.judgment_toast, Toast.LENGTH_SHORT).show();
            }
            else{
                float score = getScore();
                String msg = String.valueOf(score);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void next(){
        boolean isLast = mCurrentIndex == mQuestionBank.length - 1;
        if(!isLast){
            mCurrentIndex++;
        }
        Log.v(TAG,String.valueOf(mCurrentIndex));
        mIsCheater = false;
        updateQuestion();
        updateStatus();
    }

    private void prev(){
        boolean isFirst = mCurrentIndex == 0;
        if(!isFirst){
            mCurrentIndex--;
        }
        Log.v(TAG,String.valueOf(mCurrentIndex));
        updateQuestion();
        updateStatus();
    }

    private void updateStatus(){
        boolean hasAnswered = answers[mCurrentIndex]!=null;
        mFalseButton.setEnabled(true);
        mTrueButton.setEnabled(true);
        mPrevButton.setEnabled(true);
        mNextButton.setEnabled(true);
        mTrueButton.setEnabled(true);
        mFalseButton.setEnabled(true);
        if(mCurrentIndex==0){
            mPrevButton.setEnabled(false);
        }
        else if(mCurrentIndex==mQuestionBank.length -1){
            mNextButton.setEnabled(false);
        }
        if(hasAnswered){
            mFalseButton.setEnabled(false);
            mTrueButton.setEnabled(false);
        }
    }

    public boolean hasAllAnswered(){
        boolean result = true;
        int size = answers.length;
        for(int i=0;i<size;i++){
            if(answers[i]==null){
                result = false;
                break;
            }
        }
        return result;
    }

    public float getScore(){
        int correctCount = 0;
        int size = answers.length;
        for(int i=0;i<size;i++){
            if(answers[i]){
                correctCount++;
            }
        }
        float percent = (float) correctCount/size * 100;
        Log.v(TAG,String.valueOf(percent));
        return percent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        else if(resultCode == REQUEST_CODE_CHEAT){
            if(data==null){
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }
}