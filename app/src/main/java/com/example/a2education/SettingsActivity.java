package com.example.a2education;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    //    Define local variables
    private SeekBar seekBar;
    private TextView seekBarText;
    private Switch soundToggleSwitch;
    private SharedPreferences sharedPreferences;
    private boolean soundToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        Sets shared preferences
        sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, 0);

//        Find views
        seekBar = findViewById(R.id.questionSeekBar);
        seekBarText = findViewById(R.id.questionSeekBarDisplay);
        soundToggleSwitch = findViewById(R.id.soundSwitch);

//        Call methods to load the current data and update the screen accordingly
        loadData();
        updateViews();

//        Set a listener for the scrollbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                Plays a sound then updates the text above the scroll bar when the position on the bar is moved
                playSound();
                seekBarText.setText(String.format(Locale.ENGLISH, "Number of questions to answer: %d", seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() < 5) {
                    seekBar.setProgress(5);
                }
                onClickSaveData(seekBar);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //    Saves settings to shared preferences when called
    public void onClickSaveData(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MainActivity.NUM_QUESTIONS_TO_ASK, seekBar.getProgress());
        editor.putBoolean(MainActivity.SOUND_TOGGLE, soundToggleSwitch.isChecked());
        editor.apply();
    }

    //        Loads shared preferences data when called
    private void loadData() {
        seekBar.setProgress(sharedPreferences.getInt(MainActivity.NUM_QUESTIONS_TO_ASK, 10));
        soundToggle = sharedPreferences.getBoolean(MainActivity.SOUND_TOGGLE, true);
    }

    //    Sets the on screen views to reflect what they are in the code
    private void updateViews() {
        seekBarText.setText(String.format(Locale.ENGLISH, "Number of questions to answer: %d", seekBar.getProgress()));
        soundToggleSwitch.setChecked(soundToggle);
    }

    //    Plays a defined sound when called
    private void playSound() {
        if (soundToggle) {
            MediaPlayer media = MediaPlayer.create(this, R.raw.slider_sound);
            media.start();
        }
    }
}
