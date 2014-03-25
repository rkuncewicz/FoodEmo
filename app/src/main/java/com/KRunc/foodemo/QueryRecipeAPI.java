package com.KRunc.foodemo;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;


/**
 * Created by Robert Kuncewicz on 24/02/14.
 * FoodEmo Recipe App
 */
class QueryRecipeAPI {
    private static final String DEBUG_TAG = "YummlyHttpRequest";
    private static final String APP_ID = "4bcf4728";
    private static final String APP_KEY = "b10229904b6cd56f18b8f3264bb29b48";
    private static final String LIST_URL = "http://api.yummly.com/v1/api/recipes";
    private static final String RECIPE_URL = "http://api.yummly.com/v1/api/recipe/";
    private static final String APP_CREDENTIALS = "?_app_id="+APP_ID+"&_app_key="+APP_KEY+"&";

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.

    public static String downloadRecipe (String id) throws IOException {
        return download (RECIPE_URL+id+APP_CREDENTIALS);
    }

    public static String downloadRecipeList (String keywords) throws IOException{
        return download (LIST_URL+APP_CREDENTIALS + "q="+keywords+"&requirePictures=true");
    }

    public static String download (String myurl) throws IOException {
        System.out.println ("My url "+myurl);
        InputStream is = null;
        //TODO:Encode keywords properly
        // Only display the first 500 characters of the retrieved
        // web page content.
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            return readIt(is);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // Reads an InputStream and converts it to a JSON object.
    private static String readIt(InputStream stream) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(stream, writer, "UTF-8");
        return writer.toString();
    }
}
