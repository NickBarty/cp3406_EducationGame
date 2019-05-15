package com.example.a2education;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
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
    private int score;
    private int randomQuestionFromList;
    private int questionsAnsweredCount = 1;
    private int totalSecondsCount = 0;
    private boolean soundToggle;
    private ArrayAdapter<String> questionStrings;
    private ArrayAdapter<String> answerStrings;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private TextView questionCountDisplay;
    private TextView currentQuestion;
    private TextView currentScore;
    private TextView currentTime;
    private SensorManager sensorManager;
    private Sensor sensor;
    private SharedPreferences sharedPreferences;

    //    TODO: Implement the database & send end game data to it as well as retrieve it
//    private SQLiteDatabase db;

    //    Defines the timer displayed on the bottom of the screen
    private final Handler timerHandler = new Handler();
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            totalSecondsCount++;
            long seconds = totalSecondsCount % 60;
            long minutes = totalSecondsCount / 60;
            currentTime.setText(String.format(Locale.ENGLISH, "%d:%d", minutes, seconds));
            timerHandler.postDelayed(this, 1000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        Gets the required shared preferences
        sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, 0);

//        TODO: Get following code to use the database
//        SQLiteOpenHelper dbHelper = new DatabaseHelper(this);
//        db = dbHelper.getWritableDatabase();

//        Run methods to setup the game and run the first loop
        setupGame();
        runGameLoop();
    }

    private void setupGame() {
//        Sets up the lists that contain the questions and answers
        questionStrings = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.questions));
        answerStrings = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.answers));

//        Sets all views to their id's
        button1 = findViewById(R.id.answerButton1);
        button2 = findViewById(R.id.answerButton2);
        button3 = findViewById(R.id.answerButton3);
        button4 = findViewById(R.id.answerButton4);
        currentScore = findViewById(R.id.score);
        currentTime = findViewById(R.id.timerText);
        currentQuestion = findViewById(R.id.questionText);
        questionCountDisplay = findViewById(R.id.questionNumber);

//        Defines the sensor to use, gets stored settings, and starts the timer
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        soundToggle = sharedPreferences.getBoolean(MainActivity.SOUND_TOGGLE, true);
        timerRunnable.run();
    }

    private void runGameLoop() {
//        Set colour of score based on current score
        if (score < 0) {
            currentScore.setTextColor(Color.parseColor("#D32F2F"));
        } else if (score == 0) {
            currentScore.setTextColor(Color.parseColor("#303F9F"));
        } else currentScore.setTextColor(Color.parseColor("#388E3C"));

        if (questionsAnsweredCount > sharedPreferences.getInt(MainActivity.NUM_QUESTIONS_TO_ASK, 10)) {
            MainActivity.HIGHSCORE_SCORES.add(score);
            MainActivity.HIGHSCORE_TIME.add(totalSecondsCount);
            Intent intent = new Intent(this, EnterName.class);
            intent.putExtra("score", score);
            finish();
            startActivity(intent);
        }

//        Set the question being asked
        setQuestion();
//        Set answers for the buttons
        setAnswers();
//        Set one of the buttons to have the correct answer
        setCorrectAnswer();

    }

    private void setQuestion() {
//        Set the random question
        Random randQuestion = new Random();
        randomQuestionFromList = randQuestion.nextInt(questionStrings.getCount());
        currentQuestion.setText(questionStrings.getItem(randomQuestionFromList));
        questionCountDisplay.setText(String.format(Locale.ENGLISH, "Question %d of %d", questionsAnsweredCount, sharedPreferences.getInt(MainActivity.NUM_QUESTIONS_TO_ASK, 10)));
    }

    private void setAnswers() {
//        Set buttons to different potential answers
        ArrayList<Integer> randomAnswersToUse = new ArrayList<>();
        Random randAnswer = new Random();

//        Generates 4 random unique answers from all possible answers
        int answer;
        while (randomAnswersToUse.size() != 4) {
            answer = randAnswer.nextInt(answerStrings.getCount());
            if (!randomAnswersToUse.contains(answer)) {
                randomAnswersToUse.add(answer);
            }
        }
//        Set each button one of the unique answers
        button1.setText(answerStrings.getItem(randomAnswersToUse.get(0)));
        button2.setText(answerStrings.getItem(randomAnswersToUse.get(1)));
        button3.setText(answerStrings.getItem(randomAnswersToUse.get(2)));
        button4.setText(answerStrings.getItem(randomAnswersToUse.get(3)));
    }

    private void setCorrectAnswer() {
        //Set the correct button randomly
        Random randCorrectButton = new Random();
        int randomCorrectButton = randCorrectButton.nextInt(4);
        switch (randomCorrectButton) {
            case 1: {
                button1.setText(answerStrings.getItem(randomQuestionFromList));
                break;
            }
            case 2: {
                button2.setText(answerStrings.getItem(randomQuestionFromList));
                break;
            }
            case 3: {
                button3.setText(answerStrings.getItem(randomQuestionFromList));
                break;
            }
            case 4: {
                button4.setText(answerStrings.getItem(randomQuestionFromList));
                break;
            }
        }
    }

    public void buttonPressed(View view) {
        button1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button1.getText().toString().equals(answerStrings.getItem(randomQuestionFromList)))
                    correctAnswer();
                else {
                    wrongAnswer();
                }
            }
        });
        button2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button2.getText().toString().equals(answerStrings.getItem(randomQuestionFromList)))
                    correctAnswer();
                else {
                    wrongAnswer();
                }
            }
        });
        button3.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button3.getText().toString().equals(answerStrings.getItem(randomQuestionFromList)))
                    correctAnswer();
                else {
                    wrongAnswer();
                }
            }
        });
        button4.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button4.getText().toString().equals(answerStrings.getItem(randomQuestionFromList)))
                    correctAnswer();
                else {
                    wrongAnswer();
                }
            }
        });

    }

    private void correctAnswer() {
//        Play correct answer sound
        if (soundToggle) {
            MediaPlayer media = MediaPlayer.create(this, R.raw.correct_question);
            media.start();
        }

        score += 25;
        questionsAnsweredCount++;
        currentScore.setText(String.format(Locale.ENGLISH, "Score: %d", score));
        Toast.makeText(this, "That's correct!", Toast.LENGTH_SHORT).show();

//        Set next question
        runGameLoop();
    }

    private void wrongAnswer() {
//        Play wrong answer sound
        if (soundToggle) {
            MediaPlayer media = MediaPlayer.create(this, R.raw.wrong_question);
            media.start();
        }

        score -= 10;
        questionsAnsweredCount++;
        currentScore.setText(String.format(Locale.ENGLISH, "Score: %d", score));
        Toast.makeText(this, "Incorrect! \nCorrect answer: " + answerStrings.getItem(randomQuestionFromList), Toast.LENGTH_SHORT).show();

//        Set next question
        runGameLoop();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        Hard left or right to trigger the event, the lower the numbers the less tilt needed
        if (event.values[0] > 8 || event.values[0] < -8) {
            runGameLoop();
            totalSecondsCount++;
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

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, sensor);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this, sensor);
    }
}
