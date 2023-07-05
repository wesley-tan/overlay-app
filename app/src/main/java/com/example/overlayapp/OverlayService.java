package com.example.overlayapp;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.WindowManager;

import androidx.annotation.Nullable;

public class OverlayService extends Service {

    private OverlayView overlayView;

    @Override
    public void onCreate() {
        super.onCreate();
        // Perform initialization tasks here
        enableOverlay();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Handle logic for starting the overlay functionality here
        return START_STICKY; // Restart the service if it gets killed
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up any resources or tasks related to the overlay functionality here
        disableOverlay();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void enableOverlay() {
        overlayView = new OverlayView(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_FULLSCREEN, // Add FLAG_FULLSCREEN flag
                PixelFormat.TRANSLUCENT);
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(overlayView, params);
    }

    private void disableOverlay() {
        if (overlayView != null) {
            WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            windowManager.removeView(overlayView);
            overlayView = null;
        }
    }
}
