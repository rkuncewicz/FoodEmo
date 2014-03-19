package com.KRunc.foodemo;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by certeis on 17/03/14.
 */
public interface OnTaskCompleted {
        void onTaskCompleted(Bitmap bitmap, String recipeName);
}
