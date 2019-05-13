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
    private SeekBar seekBar;
    private TextView seekBarText;
    private Switch soundToggle;

    private boolean soundOnOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        seekBar = findViewById(R.id.questionSeekBar);
        seekBarText = findViewById(R.id.questionSeekBarDisplay);
        soundToggle = findViewById(R.id.soundSwitch);
        loadData();
        updateViews();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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

    private void playSound() {
        if (soundOnOff) {
            MediaPlayer media = MediaPlayer.create(this, R.raw.slider_sound);
            media.start();
        }
    }

    public void onClickSaveData(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MainActivity.NUM_QUESTIONS, seekBar.getProgress());
        editor.putBoolean(MainActivity.SOUND_TOGGLE, soundToggle.isChecked());
        editor.apply();
    }

    private void loadData() {
//        Loads data when activity is created
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, 0);
        seekBar.setProgress(sharedPreferences.getInt(MainActivity.NUM_QUESTIONS, 10));
        soundOnOff = sharedPreferences.getBoolean(MainActivity.SOUND_TOGGLE, true);
    }

    private void updateViews() {
        seekBarText.setText(String.format(Locale.ENGLISH, "Number of questions to answer: %d", seekBar.getProgress()));
        soundToggle.setChecked(soundOnOff);
    }
}
