package com.cs426.tetris;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class GameModeActivity extends Activity {

    private static int mode = 0;
    private static  int theme = 0;
    private LinearLayout theme_layout = null;
    Global global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_mode);
        global = Global.getInstance();
        theme_layout = (LinearLayout) findViewById(R.id.theme_select_layout);
    }

    public void click_zen(View view) {
        mode = 0;
        theme_layout.setVisibility(View.VISIBLE);
        if(global.getHighScoreNormal() < 10) {
            ImageView iv = (ImageView) findViewById(R.id.gem_option);
            iv.setVisibility(View.INVISIBLE);
        }
        Button toHide = (Button) findViewById(R.id.time_button);
        toHide.setVisibility(View.INVISIBLE);
    }

    public void click_time(View view) {
        mode = 1;
        theme_layout.setVisibility(View.VISIBLE);
        if(global.getHighScoreTime() < 10) {
            ImageView iv = (ImageView) findViewById(R.id.gem_option);
            iv.setVisibility(View.INVISIBLE);
        }
        Button toHide = (Button) findViewById(R.id.zen_button);
        toHide.setVisibility(View.INVISIBLE);
    }


    public void click_normal_theme(View view) {
        theme = 0;
        startGame();
    }

    public void click_gem(View view) {
        theme = 1;
        startGame();
    }

    public void startGame() {
        Intent intent = new Intent(this, UnityPlayerActivity.class);
        intent.putExtra("game_mode", mode);
        intent.putExtra("game_theme", theme);
        startActivity(intent);
    }

}
