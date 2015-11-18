package com.cs426.tetris;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.View;

public class ScreenShot {
    private final View view;

    
    public ScreenShot(View root) {
        this.view = root;
    }
    
    public ScreenShot(Activity activity) {
        final View contentView = activity.findViewById(android.R.id.content);
        this.view = contentView.getRootView();
    }

    public Bitmap snap() {
        Bitmap bitmap = Bitmap.createBitmap(this.view.getWidth(), this.view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}