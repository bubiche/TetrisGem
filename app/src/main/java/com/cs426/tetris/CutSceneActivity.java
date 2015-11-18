package com.cs426.tetris;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.VideoView;

import java.util.Locale;


public class CutSceneActivity extends Activity {

    private VideoView vv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cut_scene);



        vv = (VideoView) findViewById(R.id.cutscene_vv);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.cutscene);
        vv.setVideoURI(uri);

        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                donePlaying();
            }
        });
        vv.start();
    }

    public void donePlaying() {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    public void click_skip(View view) {
        vv.stopPlayback();

        donePlaying();
    }


}
