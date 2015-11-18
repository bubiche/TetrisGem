package com.cs426.tetris;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;


public class MiscActivity extends Activity {
    private AudioManager audioManager = null;
    private MediaPlayer mediaPlayer = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_misc);

        ImageView cat_anim = (ImageView) findViewById(R.id.iv_anim_misc);
        AnimationDrawable animation = (AnimationDrawable) cat_anim.getDrawable();
        animation.start();

        mediaPlayer = MediaPlayer.create(this, R.raw.taptap);
        initVolumeControl();
    }

    public void initVolumeControl() {
        try
        {
            SeekBar volBar = (SeekBar)findViewById(R.id.vol_bar);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));


            volBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onStopTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
                {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    mediaPlayer.start();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }

    public void click_map(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void click_misc_back(View view) {
        finish();
    }
}
