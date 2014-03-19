package com.KRunc.foodemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created by certeis on 08/03/14.
 */
public class ImageDownloader {
    private OnTaskCompleted listener;
    private int position;

    public ImageDownloader (OnTaskCompleted listener, int position){
        this.listener = listener;
        this.position = position;
    }

    public ImageDownloader(OnTaskCompleted listener){
        this.listener = listener;
    }

    public void download(String url, String recipeName) {
        System.out.println(url);
        BitmapDownloaderTask task = new BitmapDownloaderTask(recipeName);
        task.execute(url);
    }

    class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private String url;
        private String recipeName;

        public BitmapDownloaderTask(String recipeName) {
            this.recipeName = recipeName;
        }

        @Override
        // Actual download method, run in the task thread
        protected Bitmap doInBackground(String... params) {
            // params comes from the execute() call: params[0] is the url.
            return downloadBitmap(params[0]);
        }

        @Override
        // Once the image is downloaded, associates it to the imageView
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }
            listener.onTaskCompleted(bitmap, recipeName);
        }
    }
    static Bitmap downloadBitmap(String url) {
        final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(url);

        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode + " while retrieving bitmap from " + url);
                return null;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            // Could provide a more explicit error message for IOException or IllegalStateException
            getRequest.abort();
            Log.w("ImageDownloader: Error while retrieving bitmap from " + url, e.toString());
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return null;
    }
}
