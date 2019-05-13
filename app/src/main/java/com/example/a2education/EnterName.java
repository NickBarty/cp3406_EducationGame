package com.example.a2education;

import android.content.Intent;
import android.content.SharedPreferences;
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
    private TextInputEditText nameText;
    private int score;
    private boolean soundOnOff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_name);
        nameText = findViewById(R.id.nameText);
        TextView finalScore = findViewById(R.id.finalScore);
        score = getIntent().getIntExtra("score", 0);
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, 0);
        soundOnOff = sharedPreferences.getBoolean(MainActivity.SOUND_TOGGLE, true);

        if (score < 0) {
            finalScore.setTextColor(Color.parseColor("#D32F2F"));
        } else if (score == 0) {
            finalScore.setTextColor(Color.parseColor("#303F9F"));
        } else finalScore.setTextColor(Color.parseColor("#388E3C"));
        finalScore.setText(String.format(Locale.ENGLISH, "%d", score));
    }

    public void parseName(View view) {
        String textToParse = Objects.requireNonNull(nameText.getText()).toString();
        if (textToParse.equals("")) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
        } else {
            MainActivity.HIGHSCORE_NAMES.add(textToParse);
            finish();
            Toast.makeText(this, "Thanks for playing " + textToParse + ", view the scores!", Toast.LENGTH_LONG).show();
        }
    }

    public void sendToTwitter(View view) {
        String textToSend = String.format(Locale.ENGLISH, "I just scored %d in MathCation! Try beat my score!", score);
        Intent twitterIntent = new Intent(Intent.ACTION_SEND);
        twitterIntent.putExtra(Intent.EXTRA_TEXT, textToSend);
        twitterIntent.setType("text/plain");
        PackageManager packManager = getPackageManager();
        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(twitterIntent, PackageManager.MATCH_DEFAULT_ONLY);

        if (soundOnOff) {
            MediaPlayer media = MediaPlayer.create(this, R.raw.twitter_send);
            media.start();
        }

        boolean isResolved = false;
        for (ResolveInfo resolveInfo : resolvedInfoList) {
            if (resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")) {
                twitterIntent.setClassName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name);
                isResolved = true;
                break;
            }
        }
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
