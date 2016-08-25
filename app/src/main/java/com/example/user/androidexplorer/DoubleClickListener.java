package com.example.user.androidexplorer;

import android.view.View;
import android.view.View.OnClickListener;

/**
 * A simple double-click listener
 * Usage:
 * // Scenario 1: Setting double click listener for myView
 * myView.setOnClickListener(new DoubleClickListener() {
 *
 * @author Srikanth Venkatesh
 * @version 1.0
 * @Override public void onDoubleClick() {
 * // double-click code that is executed if the user double-taps
 * // within a span of 200ms (default).
 * }
 * });
 * <p/>
 * // Scenario 2: Setting double click listener for myView, specifying a custom double-click span time
 * myView.setOnClickListener(new DoubleClickListener(500) {
 * @Override public void onDoubleClick() {
 * // double-click code that is executed if the user double-taps
 * // within a span of 500ms (default).
 * }
 * });
 * @since 2014-09-15
 */
public abstract class DoubleClickListener implements OnClickListener {

    private static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds

    long lastClickTime = 0;

    @Override
    public void onClick(View v) {
        long clickTime = System.currentTimeMillis();
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
            onDoubleClick(v);
        } else {
            onSingleClick(v);
        }
        lastClickTime = clickTime;
    }

    public abstract void onSingleClick(View v);

    public abstract void onDoubleClick(View v);
}