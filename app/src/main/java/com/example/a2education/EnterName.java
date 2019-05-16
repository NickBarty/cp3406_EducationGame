package com.example.a2education;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EnterName extends AppCompatActivity {
    private TextInputEditText nameInput;
    private int score;
    private boolean soundToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set screen orientation to only be portrait for this activity
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_enter_name);

//        Find views
        TextView finalScore = findViewById(R.id.finalScore);
        nameInput = findViewById(R.id.nameText);

//        Get intent data
        score = getIntent().getIntExtra("score", 0);

//        Get shared preferences data
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, 0);
        soundToggle = sharedPreferences.getBoolean(MainActivity.SOUND_TOGGLE, true);

//        Set the color of the score on screen
        if (score < 0) {
            finalScore.setTextColor(Color.parseColor("#D32F2F"));
        } else if (score == 0) {
            finalScore.setTextColor(Color.parseColor("#303F9F"));
        } else finalScore.setTextColor(Color.parseColor("#388E3C"));
        finalScore.setText(String.format(Locale.ENGLISH, "%d", score));
    }

    public void parseName(View view) {
//        Checks if the name entered is blank and if not then adds it to the scores and finishes up
        String textToParse = Objects.requireNonNull(nameInput.getText()).toString();
//        Check for at least one alphanumeric character
        if (textToParse.matches(".*\\w.*")) {
            MainActivity.HIGHSCORE_NAMES.add(textToParse);
            finish();
            Toast.makeText(this, "Thanks for playing " + textToParse + ", view the scores!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendToTwitter(View view) {
//        Sets up twitter message to be sent
        String textToSend = String.format(Locale.ENGLISH, "I just scored %d in MathCation! Try beat my score!", score);
        Intent twitterIntent = new Intent(Intent.ACTION_SEND);
        twitterIntent.putExtra(Intent.EXTRA_TEXT, textToSend);
        twitterIntent.setType("text/plain");
        PackageManager packManager = getPackageManager();
        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(twitterIntent, PackageManager.MATCH_DEFAULT_ONLY);

        if (soundToggle) {
            MediaPlayer media = MediaPlayer.create(this, R.raw.twitter_send);
            media.start();
        }

        boolean isResolved = false;

//        Sends data of message to the twitter app if its installed on the device
        for (ResolveInfo resolveInfo : resolvedInfoList) {
            if (resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")) {
                twitterIntent.setClassName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name);
                isResolved = true;
                break;
            }
        }

//        Sends data of message to an available browser app instead
        if (isResolved) {
            startActivity(twitterIntent);
        } else {
            Intent otherIntent = new Intent();
            otherIntent.putExtra(Intent.EXTRA_TEXT, textToSend);
            otherIntent.setAction(Intent.ACTION_VIEW);
            otherIntent.setData(Uri.parse("https://twitter.com/intent/tweet?text=" + Uri.encode(textToSend)));
            startActivity(otherIntent);
            Toast.makeText(this, "Twitter app isn’t found, using browser instead", Toast.LENGTH_LONG).show();
        }
    }
}
