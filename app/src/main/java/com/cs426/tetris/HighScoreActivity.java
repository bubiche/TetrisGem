package com.cs426.tetris;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;

import org.json.JSONException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class HighScoreActivity extends Activity {
    private static final int REQ_CODE_TAKE_PICTURE = 90210;
    private static final Integer[] backgrounds = {R.drawable.hot_bg, R.drawable.cold_bg};
    private String city = "Saigon";
    private LinearLayout bg;
    private double phoneLat = 10.768451;
    private double phoneLong = 106.6943626;
    ShareButton shareButton = null;
    private MediaPlayer mediaPlayer = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_high_score);

        shareButton = (ShareButton)findViewById(R.id.fb_share_button);
        Global global = Global.getInstance();
        ImageView gem_iv = (ImageView) findViewById(R.id.hs_unlock_iv);
        bg = (LinearLayout) findViewById(R.id.high_score_bg);

        TextView hs_normal = (TextView) findViewById(R.id.hs_normal_tv);
        TextView hs_time = (TextView) findViewById(R.id.hs_time_tv);

        hs_normal.setText(String.valueOf(global.getHighScoreNormal()));
        hs_time.setText(String.valueOf(global.getHighScoreTime()));

        gem_iv.setVisibility(global.getHighScoreNormal() < 10 && global.getHighScoreTime() < 10 ? View.INVISIBLE : View.VISIBLE);

        initLocation();

        JSONWeatherTask task = new JSONWeatherTask();
        task.execute(city);

        mediaPlayer = MediaPlayer.create(this, R.raw.nyan);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    public void back_click(View view) {
        finish();
    }

    public void click_camera(View view) {
        Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(picIntent, REQ_CODE_TAKE_PICTURE);
        }
        catch (Exception e) {
            Log.e("CAMERA", "DEVICE DOES NOT SUPPORT CAMERA");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == REQ_CODE_TAKE_PICTURE && resultCode == RESULT_OK) {
            Bitmap bmp = (Bitmap) intent.getExtras().get("data");

            BitmapDrawable bmp_d = new BitmapDrawable(getResources(),bmp);
            bg.setBackground(bmp_d);
        }
    }

    public void click_ss(View view) {
        ScreenShot ss = new ScreenShot(bg);
        Bitmap screenShot = ss.snap();
        SharePhoto photo = new SharePhoto.Builder().setBitmap(screenShot).build();
        SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
        /*
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://developers.facebook.com"))
                .build();
        */
        shareButton.setShareContent(content);
        Log.i("INITIALIZE: ", "DONE!");
    }

    private class JSONWeatherTask extends AsyncTask<String, Void, Double> {

        @Override
        protected Double doInBackground(String... params) {
            Double res = 303.17;
            String data = ( (new WeatherHTTPClient()).getWeatherData(params[0]));

            try {
                res = JSONWeatherParser.getTemp(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(Double temp) {
            super.onPostExecute(temp);
            temp -= 273.15;
            Log.i("TEMP: ", String.valueOf(temp));
            bg.setBackgroundResource(temp > 30 ? backgrounds[0] : backgrounds[1]);

            TextView temp_tv = (TextView) findViewById(R.id.temperature_tv);
            temp_tv.setText(Html.fromHtml(String.valueOf(round(temp)) + "<sup>C</sup>"));
        }
    }

    public void initLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc == null) {
            loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (loc != null) {
            phoneLat = loc.getLatitude();
            phoneLong = loc.getLongitude();
        }

        Log.i("COORD:", "LAT: " + String.valueOf(phoneLat) + " LNG: " + String.valueOf(phoneLong));
        try {
            Geocoder gcd = new Geocoder(this, Locale.ENGLISH);
            List<Address> addresses = gcd.getFromLocation(phoneLat, phoneLong, 1);
            if (addresses.size() > 0) {
                String tcity = addresses.get(0).getAdminArea();
                Log.i("LOCATION: ", "" + tcity);
                if(tcity != null) {
                    city = tcity;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double round(double val) {
        BigDecimal bd = new BigDecimal(val);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mediaPlayer.start();
    }
}
