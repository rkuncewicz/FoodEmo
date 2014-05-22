package com.KRunc.foodemo;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import Interfaces.*;

/**
 * Created by certeis on 08/03/14.
 */
public class ImageAdapter extends BaseAdapter implements Adapter {
    private LruCache<String, Bitmap> mMemoryCache;
    private final List<Recipe> recipes;
    private final AbstractMap imageViewMap;
    private final LayoutInflater layoutInflater;
    private static final int aKB = 1024;


    public ImageAdapter(Context c, List<Recipe> recipes, FragmentManager fragmentManager) {
        this.recipes = recipes;
        imageViewMap = new HashMap();
        layoutInflater = LayoutInflater.from(c);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / aKB);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        RetainFragment retainFragment = RetainFragment.findOrCreateRetainFragment(fragmentManager);
        mMemoryCache = retainFragment.mRetainedCache;
        if (mMemoryCache == null) {
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                protected int sizeOf(String key, Bitmap bitmap) {
                    // The cache size will be measured in kilobytes rather than
                    // number of items.
                    return bitmap.getByteCount() / aKB;
                }
            };
            retainFragment.mRetainedCache = mMemoryCache;

            Iterator recipeItr = recipes.iterator();
            while (recipeItr.hasNext()) {
                Recipe recipe = (Recipe) recipeItr.next();
                Downloader downloadImg = new ImageDownloader (this);
                String[] urls = recipe.getPictureUrls();
                downloadImg.download(urls[0], recipe.getName());
            }
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
            view = convertView;
        }

        int columnWidth = parent.getWidth() / 2;

        imageView = (ImageView)view.findViewById(R.id.image);
        textView = (TextView)view.findViewById(R.id.title);

        imageView.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth));
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
        addBitmapToMemoryCache(recipeName, bitmap);

        ImageView imageView = (ImageView) imageViewMap.get(recipeName);
        if (imageView != null) {
            System.out.println("Adding and setting bitmap for " +recipeName);
            imageView.setImageBitmap(bitmap);
            this.notifyDataSetChanged();
        }
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null && bitmap != null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}

class RetainFragment extends Fragment {
    private static final String TAG = "RetainFragment";
    public LruCache<String, Bitmap> mRetainedCache;

    public RetainFragment() {}

    public static RetainFragment findOrCreateRetainFragment(FragmentManager fm) {
        RetainFragment fragment = (RetainFragment) fm.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new RetainFragment();
            fm.beginTransaction().add(fragment, TAG).commit();
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
}