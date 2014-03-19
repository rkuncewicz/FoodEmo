package com.KRunc.foodemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by certeis on 08/03/14.
 */
public class ImageAdapter extends BaseAdapter implements OnTaskCompleted {
    private Context mContext;
    private LruCache<String, Bitmap> mMemoryCache;
    private List<Recipe> recipes;
    private HashMap imageViewMap;
    private LayoutInflater layoutInflater;


    public ImageAdapter(Context c, List<Recipe> recipes) {
        this.recipes = recipes;
        imageViewMap = new HashMap();
        layoutInflater = LayoutInflater.from(c);
        mContext = c;

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

        downloadRecipes();
    }

    private void downloadRecipes(){
        Iterator recipeItr = recipes.iterator();
        while (recipeItr.hasNext()) {
            Recipe recipe = (Recipe) recipeItr.next();
            ImageDownloader downloadImg = new ImageDownloader (this);
            String[] urls = recipe.getPictureUrls();
            downloadImg.download(urls[0], recipe.getName());
        }
    }

    public int getCount() {
        return recipes.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        TextView textView;
        View view;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            view = layoutInflater.inflate(R.layout.thumb_image, null);
        }
        else {
            view = (View)convertView;
            //imageView = (ImageView) convertView;
        }

        int columnWidth = parent.getWidth() / 2;

        imageView = (ImageView)view.findViewById(R.id.image);
        textView = (TextView)view.findViewById(R.id.title);

        imageView.setLayoutParams(new LinearLayout.LayoutParams(columnWidth, columnWidth));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Recipe recipe = recipes.get(position);
        String recipeName = recipe.getName();

        textView.setText(String.valueOf(recipeName));
        imageViewMap.put (recipeName, imageView);
        Bitmap bitmap = getBitmapFromMemCache(recipeName);
        if (bitmap != null){
            imageView.setImageBitmap(bitmap);
        }

        return view;
    }

    public void onTaskCompleted (Bitmap bitmap, String recipeName) {
        ImageView imageView = (ImageView) imageViewMap.get(recipeName);
        if (imageView != null) {
            if (imageView != null) {
                addBitmapToMemoryCache(recipeName, bitmap);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}