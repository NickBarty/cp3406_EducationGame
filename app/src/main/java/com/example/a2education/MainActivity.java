package com.example.a2education;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //        Set the shared preferences for the app
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String NUM_QUESTIONS_TO_ASK = "numQuestions";
    public static final String SOUND_TOGGLE = "soundToggle";

    //    Set lists to be used to keep track of scores while database isn't implemented fully
    public static final ArrayList<String> HIGHSCORE_NAMES = new ArrayList<>();
    public static final ArrayList<Integer> HIGHSCORE_SCORES = new ArrayList<>();
    public static final ArrayList<Integer> HIGHSCORE_TIME = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //    Start GameActivity
    public void play(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    //    Display a dialogue on how to play
    public void howToPlayDisplay(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("How to play");
        alertDialog.setMessage("This is how to play:" +
                "\n\n- Change settings in settings menu" +
                "\n\n- Tap 'High Scores' to see high scores" +
                "\n\n- Tap PLAY to play!" +
                "\n\n- Answer all questions as fast as possible!" +
                "\n\n- You will be prompted to enter your name when all questions are completed" +
                "\n\n- Tilt the device hard left or right to find a new question, beware this adds time to the timer!");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    //    Display a dialogue with the scores for the current session
    public void scoresDisplay(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Scores For This Session!");
        String names = HIGHSCORE_NAMES.toString();
        String scores = HIGHSCORE_SCORES.toString();
        String times = HIGHSCORE_TIME.toString();
        alertDialog.setMessage("The following are scores from this session:" +
                "\n\n- Individuals are separated by commas" +
                "\n- Times are in seconds" +
                "\n\nName - " + names +
                "\n\nScore - " + scores +
                "\n\nTime" + times);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    //    Start the SettingsActivity
    public void settings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
