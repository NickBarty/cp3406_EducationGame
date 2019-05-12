package com.example.a2education;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.Objects;

public class EnterName extends AppCompatActivity {
    TextInputEditText nameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_name);
        nameText = findViewById(R.id.nameText);
    }

    public void parseName(View view) {
        String textToParse = Objects.requireNonNull(nameText.getText()).toString();
        if (textToParse.equals("")) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
        } else {
            MainActivity.HIGHSCORE_NAMES.add(textToParse);
            finish();
            Toast.makeText(this, "Thanks for playing, view the scores!", Toast.LENGTH_SHORT).show();
        }
    }
}
