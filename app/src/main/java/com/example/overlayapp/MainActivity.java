package com.example.overlayapp;

import android.view.View;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.Toast;
import android.os.Build;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.view.MotionEvent;
import android.net.wifi.WifiManager;
import android.content.Context;

public class MainActivity extends AppCompatActivity {

    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1;
    private OverlayView overlayView;
    private WifiManager.WifiLock wifiLock; // Declare the wifiLock variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overlayView = findViewById(R.id.overlayView);
    }

    private void enableOverlay() {
        if (Settings.canDrawOverlays(this)) {
            if (!overlayView.isStatusBarBlocked()) {
                overlayView.setStatusBarBlocked(true); // Block the status bar
                updateOverlayStatus(true);
                overlayView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // Block touch events on the overlay view to prevent interaction with the status bar
                        return true;
                    }
                });

                // Acquire a WifiLock to keep the WiFi radio awake
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiManager.WifiLock wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "MyWifiLock");
                wifiLock.acquire();
            }
        }
    }

    private void hideStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public void createOverlay(View view) {
        // Check if the overlay view is available
        if (overlayView != null) {
            if (Settings.canDrawOverlays(this)) {
                // Overlay permission is granted
                if (!overlayView.isStatusBarBlocked()) {
                    overlayView.setStatusBarBlocked(true); // Block the status bar
                    updateOverlayStatus(true);
                    hideStatusBar(); // Hide the status bar

                    // Call enableOverlay() to acquire the WifiLock
                    enableOverlay();
                } else {
                    Toast.makeText(this, "Overlay is already enabled", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Overlay permission is not granted
                // Request the permission from the user
                Intent overlayIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(overlayIntent, OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }
    public void stopOverlay(View view) {
        overlayView.setStatusBarBlocked(false); // Unblock the status bar
        updateOverlayStatus(false);

        // Release the WifiLock only if it has been acquired
        if (wifiLock != null && wifiLock.isHeld()) {
            wifiLock.release();
        }
        Toast.makeText(this, "Overlay stopped", Toast.LENGTH_SHORT).show();
    }

    public void openSettings(View view) {
        Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        settingsIntent.setData(uri);
        startActivity(settingsIntent);
    }

    private void updateOverlayStatus(boolean enabled) {
        TextView overlayStatusTextView = findViewById(R.id.overlayStatusTextView);
        if (enabled) {
            overlayStatusTextView.setText("Overlay: On");
        } else {
            overlayStatusTextView.setText("Overlay: Off");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                // Overlay permission is granted
                overlayView.setStatusBarBlocked(true); // Block the status bar
                updateOverlayStatus(true);
            } else {
                Toast.makeText(this, "Please allow OverlayApp to 'display over other apps'", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (overlayView != null) {
            // Release the WifiLock
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiManager.WifiLock wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "MyWifiLock");
            wifiLock.release();
        }
    }
}
