package com.example.overlayapp;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OverlayView extends ViewGroup {

    private boolean isStatusBarBlocked = false; // Flag to determine if the status bar is blocked
    private TextView overlayStatusTextView; // Reference to the overlay status TextView


    public boolean isStatusBarBlocked() {
        return isStatusBarBlocked;
    }
    public OverlayView(Context context) {
        super(context);
        init();
    }

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundColor(Color.TRANSPARENT); // Set the overlay background color to transparent
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isStatusBarBlocked()) {
            // Block touch events to prevent interaction with the status bar
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // Layout the child views within the OverlayView
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.layout(left, top, right, bottom);
        }
    }

    public void setStatusBarBlocked(boolean blocked) {
        isStatusBarBlocked = blocked;
    }

    public void setOverlayStatusTextView(TextView textView) {
        overlayStatusTextView = textView;
    }

    public void updateOverlayStatus(boolean enabled) {
        if (overlayStatusTextView != null) {
            if (enabled) {
                overlayStatusTextView.setText("Overlay is enabled");
            } else {
                overlayStatusTextView.setText("Overlay is disabled");
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isStatusBarBlocked()) {
            // Block touch events to prevent interaction with the status bar
            return true;
        }
        return super.onTouchEvent(event);
    }
}
