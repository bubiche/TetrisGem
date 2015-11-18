package com.cs426.tetris;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class WelcomeActivity extends Activity {

    private static final String SCORE_FILE = "highscrores";
    private static final Integer[] colors = {0xff3498db, 0xff8e44ad, 0xff1abc9c, 0xff2c3e50, 0xfff1c40f, 0xffd35400, 0xffe74c3c, 0xff95a5a6};
    protected static final int RESULT_SPEECH = 1;
    private TextToSpeech tts;

    private LinearLayout bg;
    private ShakeListener mShaker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Global global = Global.getInstance();
        bg = (LinearLayout) findViewById(R.id.welcome_bg);
        Random random = new Random();
        int colorIndex = random.nextInt(colors.length);
        bg.setBackgroundColor(colors[colorIndex]);

        File file = getBaseContext().getFileStreamPath(SCORE_FILE);
        if(!file.exists()) {
            try {
                PrintStream fout = new PrintStream(openFileOutput(SCORE_FILE, Context.MODE_PRIVATE));
                fout.println(0);
                fout.println(0);
                fout.close();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

        try {
            Scanner scan = new Scanner(openFileInput(SCORE_FILE));
            global.setHighScoreNormal(scan.nextInt());
            global.setHighScoreTime(scan.nextInt());
            scan.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        mShaker = new ShakeListener(this);
        mShaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
            @Override
            public void onShake() {
                Random random = new Random();
                int colorIndex = random.nextInt(colors.length);
                bg.setBackgroundColor(colors[colorIndex]);
            }
        });

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    String speech = "Welcome";
                    Log.i("TTS", "SPEAKING");
                    tts.speak(speech,TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }


    public void click_start_game(View view) {
        Intent intent = new Intent(this, GameModeActivity.class);
        startActivity(intent);
    }

    public void click_logo(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

        try {
            startActivityForResult(intent, RESULT_SPEECH);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Your device doesn't support Speech to Text",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    String res = text.get(0);
                    Log.i("STT: ", res);
                    if(res.matches(".*\\bplay\\b.*") || res.matches(".*\\bleg\\b.*")) {
                        Intent intent = new Intent(this, GameModeActivity.class);
                        startActivity(intent);
                    }
                    else if(res.matches(".*\\brecord\\b.*") || res.matches(".*\\bhigh\\b.*")) {
                        viewHighScores();
                    }
                    else if(res.matches(".*\\bmore\\b.*")) {
                        Intent intent = new Intent(this, MiscActivity.class);
                        startActivity(intent);
                    }
                }
                break;
            }
        }
    }

    public void click_hs(View view) {
        viewHighScores();
    }

    public void viewHighScores() {
        Intent intent = new Intent(this, HighScoreActivity.class);
        startActivity(intent);
    }

    public void click_more(View view) {
        Intent intent = new Intent(this, MiscActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPause() {
        mShaker.pause();
        if(tts != null){
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        mShaker.resume();
        super.onResume();
    }
}
