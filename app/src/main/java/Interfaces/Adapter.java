package Interfaces;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by certeis on 21/05/14.
 */
public interface Adapter extends OnTaskCompleted {
    public int getCount();
    public Object getItem(int position);
    public long getItemId(int position);
    public View getView(int position, View convertView, ViewGroup parent);
    public void onTaskCompleted (Bitmap bitmap, String recipeName);
    public void addBitmapToMemoryCache(String key, Bitmap bitmap);
    public Bitmap getBitmapFromMemCache(String key);
}
