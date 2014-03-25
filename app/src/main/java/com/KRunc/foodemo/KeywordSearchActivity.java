package com.KRunc.foodemo;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KeywordSearchActivity extends ActionBarActivity {

    private TextView textView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyword_search);
        textView = (TextView) findViewById(R.id.testText);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.keyword_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    public void searchRecipes(View view){
        textView.setText("Searching...");

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        EditText editText = (EditText) findViewById(R.id.edit_message);
        String keywords = "";
        if (editText.getText() != null) keywords = editText.getText().toString();

        System.out.println(keywords);
        if (networkInfo != null && networkInfo.isConnected()) {
            DownloadWebpageTask task = new DownloadWebpageTask();
            task.execute(keywords);
        } else {
            //TODO: Pretty error handling
            textView.setText("No network connection available.");
        }
    }

    void gotoRecipeList(ArrayList<Recipe> recipes){
        Intent intent = new Intent(this, RecipeListActivity.class);
        intent.putParcelableArrayListExtra("recipes", recipes);
        startActivity(intent);
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return QueryRecipeAPI.downloadRecipeList(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            JSONParser parser = new JSONParser();
            ArrayList<Recipe> recipeArray = new ArrayList<Recipe>();
            try {
                JSONObject obj = (JSONObject) parser.parse(result);
                JSONArray matches = (JSONArray) obj.get("matches");
                for (Object matche : matches) {
                    JSONObject match = (JSONObject) matche;
                    String name = match.get("recipeName").toString();

                    String id = match.get("id").toString();

                    JSONArray urls = (JSONArray) match.get("smallImageUrls");
                    ArrayList<String> imageUrls = new ArrayList<String>();
                    for (Object url : urls) {
                        imageUrls.add((String) url);
                    }

                    recipeArray.add(new Recipe(name, id, imageUrls));
                }
            }
            catch(ParseException pe) {
                System.out.println("position: " + pe.getPosition());
                System.out.println(pe);
            }
            gotoRecipeList(recipeArray);
        }
    }
}
