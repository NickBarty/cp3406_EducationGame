package com.example.a2education;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements SensorEventListener {
    ArrayAdapter<String> questionStrings, answerStrings;
    Button button1, button2, button3, button4;
    TextView questionDisplay, questionCountDisplay, scoreDisplay, timerText;
    int randomQuestion, score, questionCount = 1, timeCount = 0;
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            timeCount++;
            long seconds = timeCount % 60;
            long minutes = timeCount / 60;
            timerText.setText(String.format(Locale.ENGLISH, "%d:%d", minutes, seconds));
            timerHandler.postDelayed(this, 1000);
        }
    };
    private SensorManager sensorManager;
    private Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        timerText = findViewById(R.id.timerText);
        timerRunnable.run();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        setupGame();
        runGameLoop();
    }

    public void setupGame() {
        questionStrings = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.questions));
        answerStrings = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.answers));
        scoreDisplay = findViewById(R.id.score);
        questionDisplay = findViewById(R.id.questionText);
        questionCountDisplay = findViewById(R.id.questionNumber);
        button1 = findViewById(R.id.answerButton1);
        button2 = findViewById(R.id.answerButton2);
        button3 = findViewById(R.id.answerButton3);
        button4 = findViewById(R.id.answerButton4);
    }

    public void buttonPressed(View view) {
        button1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button1.getText().toString().equals(answerStrings.getItem(randomQuestion)))
                    correctAnswer();
                else {
                    wrongAnswer();
                }
            }
        });
        button2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button2.getText().toString().equals(answerStrings.getItem(randomQuestion)))
                    correctAnswer();
                else {
                    wrongAnswer();
                }
            }
        });
        button3.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button3.getText().toString().equals(answerStrings.getItem(randomQuestion)))
                    correctAnswer();
                else {
                    wrongAnswer();
                }
            }
        });
        button4.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button4.getText().toString().equals(answerStrings.getItem(randomQuestion)))
                    correctAnswer();
                else {
                    wrongAnswer();
                }
            }
        });

    }

    public void correctAnswer() {
        score += 10;
        questionCount++;
        scoreDisplay.setText(String.format(Locale.ENGLISH, "Score: %d", score));
        Toast.makeText(this, "That's correct!", Toast.LENGTH_SHORT).show();
        clearAllButtons();

//        Next question
        runGameLoop();
    }

    public void wrongAnswer() {
        score -= 5;
        questionCount++;
        scoreDisplay.setText(String.format(Locale.ENGLISH, "Score: %d", score));
        Toast.makeText(this, "Incorrect! \nCorrect answer: " + answerStrings.getItem(randomQuestion), Toast.LENGTH_SHORT).show();
        clearAllButtons();
        runGameLoop();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values[0] > 9 || event.values[0] < -9) {
            runGameLoop();
            timeCount++;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//        Ignore
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void runGameLoop() {
//        Set colour of score based on current score
        if (score < 0) {
            scoreDisplay.setTextColor(Color.parseColor("#D32F2F"));
        } else if (score == 0) {
            scoreDisplay.setTextColor(Color.parseColor("#303F9F"));
        } else scoreDisplay.setTextColor(Color.parseColor("#388E3C"));

        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, 0);
        if (questionCount > sharedPreferences.getInt(MainActivity.NUM_QUESTIONS, 10)) {
            MainActivity.HIGHSCORE_SCORES.add(score);
            MainActivity.HIGHSCORE_TIME.add(timeCount);
            Intent intent = new Intent(this, EnterName.class);
            startActivity(intent);
            finish();
        }

//        Set the question being asked
        setQuestion();
//        Set answers for the buttons
        setAnswers();
//        Set one of the buttons to have the correct answer
        setCorrectAnswer();

    }

    public void setQuestion() {
//        Set the random question
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, 0);
        Random randQuestion = new Random();
        randomQuestion = randQuestion.nextInt(questionStrings.getCount());
        questionDisplay.setText(questionStrings.getItem(randomQuestion));
        questionCountDisplay.setText(String.format(Locale.ENGLISH, "Question %d of %d", questionCount, sharedPreferences.getInt(MainActivity.NUM_QUESTIONS, 10)));
    }

    public void setAnswers() {
//        Set buttons to different potential answers
        ArrayList<Integer> randomAnswersToUse = new ArrayList<>();
        Random randAnswer = new Random();
        int answer;
        while (randomAnswersToUse.size() != 4) {
            answer = randAnswer.nextInt(answerStrings.getCount());
            if (!randomAnswersToUse.contains(answer)) {
                randomAnswersToUse.add(answer);
            }
        }
        button1.setText(answerStrings.getItem(randomAnswersToUse.get(0)));
        button2.setText(answerStrings.getItem(randomAnswersToUse.get(1)));
        button3.setText(answerStrings.getItem(randomAnswersToUse.get(2)));
        button4.setText(answerStrings.getItem(randomAnswersToUse.get(3)));
    }

    public void setCorrectAnswer() {
        //Set the correct button randomly
        // - This has a bug where if the chosen incorrect answers happen to randomly choose the
        // correct answer then there will be 2 of the same answers
        Random randCorrectButton = new Random();
        int randomCorrectButton = randCorrectButton.nextInt(4);
        switch (randomCorrectButton) {
            case 1: {
                button1.setText(answerStrings.getItem(randomQuestion));
                break;
            }
            case 2: {
                button2.setText(answerStrings.getItem(randomQuestion));
                break;
            }
            case 3: {
                button3.setText(answerStrings.getItem(randomQuestion));
                break;
            }
            case 4: {
                button4.setText(answerStrings.getItem(randomQuestion));
                break;
            }
        }
    }

    public void clearAllButtons() {
        button1.setText("");
        button2.setText("");
        button3.setText("");
        button4.setText("");
    }


}
