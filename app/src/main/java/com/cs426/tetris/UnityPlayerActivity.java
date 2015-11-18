package com.cs426.tetris;

import com.unity3d.player.*;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.PrintStream;

public class UnityPlayerActivity extends Activity
{
	private static final int CUTSCENE = 123;
	protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code
	private int sprite = 0;
	private int mode = 0;
	private View dialogView = null;
	private ShakeListener mShaker;
	private static Vibrator vibrator;
	private FrameLayout over_layout = null;
	private TextView player_score_tv = null;
	private TextView best_scrore_tv = null;
	private ProgressBar progressBar = null;
	private CountDownTimer countDownTimer = null;
	private static MediaPlayer mPlayer = null;

	// Setup activity layout
	@Override protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		getWindow().setFormat(PixelFormat.RGBX_8888); // <--- This makes xperia play happy
		setContentView(R.layout.game_layout);

		Intent intent = getIntent();
		sprite = intent.getIntExtra("game_theme", 0);
		mode = intent.getIntExtra("game_mode", 0);
		progressBar = (ProgressBar) findViewById(R.id.timer_pb);
		initGameMode();

		vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		mUnityPlayer = new UnityPlayer(this);
		int glesMode = mUnityPlayer.getSettings().getInt("gles_mode", 1);
		boolean trueColor8888 = false;
		mUnityPlayer.init(glesMode, trueColor8888);

		over_layout = (FrameLayout) findViewById(R.id.game_over_form);
		player_score_tv = (TextView) findViewById(R.id.player_score_tv);
		best_scrore_tv = (TextView) findViewById(R.id.best_score_tv);



		RelativeLayout layout = (RelativeLayout) findViewById(R.id.relayout);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		layout.addView(mUnityPlayer.getView(), 0, lp);

		ImageView left = (ImageView) findViewById(R.id.left_arrow_iv);
		left.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT));
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
					mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT));
					return true;
				}
				return false;
			}
		});

		ImageView right = (ImageView) findViewById(R.id.right_arrow_iv);
		right.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
					mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_RIGHT));
					return true;
				}
				return false;
			}
		});

		ImageView rotate = (ImageView) findViewById(R.id.rotate_arrow_iv);
		rotate.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP));
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
					mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP));
					return true;
				}
				return false;
			}
		});

		ImageView down = (ImageView) findViewById(R.id.down_arrow_iv);
		down.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN));
				mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_DOWN));
			}
		});

		down.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SPACE));
				mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SPACE));
				return true;
			}
		});


		mShaker = new ShakeListener(this);
		mShaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
			@Override
			public void onShake() {
				if(vibrator.hasVibrator())
					vibrator.vibrate(200);
				mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_B));
				mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_B));
			}
		});

		mPlayer = MediaPlayer.create(UnityPlayerActivity.this, R.raw.clock);
		mPlayer.setVolume(100,100);
	}

	// Quit Unity
	@Override protected void onDestroy ()
	{
		mPlayer.stop();
		mUnityPlayer.quit();
		super.onDestroy();
	}

	// Pause Unity
	@Override protected void onPause()
	{
		mShaker.pause();
		super.onPause();
		mUnityPlayer.pause();
	}

	// Resume Unity
	@Override protected void onResume()
	{
		mShaker.resume();
		super.onResume();
		mUnityPlayer.resume();
	}

	// This ensures the layout will be correct.
	@Override public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		mUnityPlayer.configurationChanged(newConfig);
	}

	// Notify Unity of the focus change.
	@Override public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		mUnityPlayer.windowFocusChanged(hasFocus);
	}

	// For some reason the multiple keyevent type is not supported by the ndk.
	// Force event injection by overriding dispatchKeyEvent().
	@Override public boolean dispatchKeyEvent(KeyEvent event)
	{
		if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
			return mUnityPlayer.injectEvent(event);
		return super.dispatchKeyEvent(event);
	}

	// Pass any events not handled by (unfocused) views straight to UnityPlayer
	@Override public boolean onKeyUp(int keyCode, KeyEvent event)     { return mUnityPlayer.injectEvent(event); }
	@Override public boolean onKeyDown(int keyCode, KeyEvent event)   { return mUnityPlayer.injectEvent(event); }
	@Override public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
	/*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }

	public void gameOver(int rowsCleared) {
		final int rc = rowsCleared;
		Log.i("MyActivity", "Got " + rowsCleared);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Global global = Global.getInstance();
				over_layout.setVisibility(View.VISIBLE);
				if(vibrator.hasVibrator())
					vibrator.vibrate(500);

				player_score_tv.setText(String.valueOf(rc));
				if(mode == 0) {
					if(rc > global.getHighScoreNormal()) {
						global.setHighScoreNormal(rc);
					}
					best_scrore_tv.setText(String.valueOf(global.getHighScoreNormal()));
				}
				else if(mode == 1) {
					if(rc > global.getHighScoreTime()) {
						global.setHighScoreTime(rc);
					}
					best_scrore_tv.setText(String.valueOf(global.getHighScoreTime()));
				}


				try {
					PrintStream fout = new PrintStream(openFileOutput("highscrores", Context.MODE_PRIVATE));
					fout.println(global.getHighScoreNormal());
					fout.println(global.getHighScoreTime());
					fout.close();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});e
	}

	public void click_pause_game(View view) {
		dialogView = getLayoutInflater().inflate(R.layout.pause_game_dialog, null);
		final AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();
		ImageView imgButtonStop = (ImageView) dialogView.findViewById(R.id.pause_back_to_main);
		imgButtonStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UnityPlayerActivity.this, WelcomeActivity.class);
				int pendingId = 1234;
				PendingIntent pendingIntent = PendingIntent.getActivity(UnityPlayerActivity.this, pendingId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				AlarmManager mgr = (AlarmManager)UnityPlayerActivity.this.getSystemService(Context.ALARM_SERVICE);
				mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
				System.exit(0);
			}
		});

		ImageView imgButtonResume = (ImageView) dialogView.findViewById(R.id.pause_resume);
		imgButtonResume.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_O));
				mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_O));
				dialog.dismiss();
			}
		});

		ImageView imgButtonRestart = (ImageView) dialogView.findViewById(R.id.pause_restart);
		imgButtonRestart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_R));
				mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_R));
				dialog.dismiss();
			}
		});


		mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_P));
		mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_P));
		dialog.setCanceledOnTouchOutside(true);
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_O));
				mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_O));
			}
		});
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_O));
				mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_O));
			}
		});
		dialog.show();
	}

	public void click_over_try_again(View view) {
		over_layout.setVisibility(View.INVISIBLE);
		mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_R));
		mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_R));
		Intent intent = new Intent(this, CutSceneActivity.class);
		startActivityForResult(intent, CUTSCENE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
			case (CUTSCENE) : {
				if (resultCode == Activity.RESULT_OK) {
					//RelativeLayout game_screen_layout = (RelativeLayout) findViewById(R.id.relayout);
					//LinearLayout buttons_layout = (LinearLayout) findViewById(R.id.button_layout);
					//game_screen_layout.setRotation(180);
					//buttons_layout.setRotation(180);
					//mUnityPlayer.resume();
					Log.i("MyActivity", "Restarting");
					initGameMode();
					//if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
						//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
					//}
					//else {
						//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					//}
					//mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_R));
					//mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_R));
				}
				break;
			}
		}
	}

	public void click_over_return_to_main(View view) {
		Intent intent = new Intent(UnityPlayerActivity.this, WelcomeActivity.class);
		int pendingId = 1234;
		PendingIntent pendingIntent = PendingIntent.getActivity(UnityPlayerActivity.this, pendingId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager mgr = (AlarmManager)UnityPlayerActivity.this.getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
		System.exit(0);
	}

	public void initTimer() {
		int time = 180000;
		progressBar.setProgress(180);
		countDownTimer = new CountDownTimer(time,1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				progressBar.setProgress((int)millisUntilFinished/1000);
				if(millisUntilFinished < 31000 && !mPlayer.isPlaying()) {

					mPlayer.start();
				}
			}

			@Override
			public void onFinish() {
				mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_V));
				mUnityPlayer.injectEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_V));
			}
		};
		countDownTimer.start();
	}

	public void initGameMode() {
		if(mode == 0) {
			progressBar.setVisibility(View.INVISIBLE);
		}
		else {
			progressBar.setVisibility(View.VISIBLE);
			initTimer();
		}
	}
}
