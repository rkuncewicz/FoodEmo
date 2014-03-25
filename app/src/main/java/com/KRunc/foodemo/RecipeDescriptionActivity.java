package com.KRunc.foodemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.achartengine.GraphicalView;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public class RecipeDescriptionActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_description);

        Recipe recipe = getIntent().getParcelableExtra("recipe");
        TextView title = (TextView) findViewById(R.id.recipe_title);
        if (title != null) title.setText(recipe.getName());

        ImageView image = (ImageView) findViewById(R.id.recipe_image);
        Bitmap recipeImage = getIntent().getParcelableExtra("recipeImage");
        image.setImageBitmap(recipeImage);

        getRecipe(recipe.getId());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.recipe_description, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getRecipe (String id){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            DownloadWebpageTask task = new DownloadWebpageTask();
            task.execute(id);
        } else {
            //TODO: Pretty error handling
        }
    }

    public void displayInfo (Recipe recipe) {
        if (recipe != null){
            TextView ingredientView = (TextView) findViewById(R.id.recipe_ingredients);
            String[] ingredientLines = recipe.getIngredientLines();
            String ingredientString = "";
            for (int i=0; i < ingredientLines.length; i++){
                ingredientString += "&#8226; "+ingredientLines[i]+"<br/>";
            }
            ingredientView.setMovementMethod(new ScrollingMovementMethod());
            ingredientView.append(Html.fromHtml(ingredientString));

            TextView cookTimeView = (TextView) findViewById(R.id.total_time);
            cookTimeView.append(recipe.getTotalTime());
        }
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... id) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return QueryRecipeAPI.downloadRecipe(id[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            JSONParser parser = new JSONParser();
            try {
                JSONObject match = (JSONObject) parser.parse(result);
                String name = match.get("name").toString();
                String id = match.get("id").toString();
                String totalTime = match.get("totalTime").toString();
                long rating = (Long) match.get("rating");
                long numOfServing = (Long) match.get("numberOfServings");
                long totalTimeInSeconds = (Long) match.get("totalTimeInSeconds");

                ArrayList<String> sources  = new ArrayList<String>();
                JSONObject sourceJSON = (JSONObject) match.get("source");
                String source = (String) sourceJSON.get("sourceRecipeUrl");

                ArrayList<String> imageUrls = new ArrayList<String>();

                JSONArray ingredientLines = (JSONArray) match.get("ingredientLines");
                ArrayList<String> ingredients = new ArrayList<String>();
                for (Object line : ingredientLines) {
                    ingredients.add((String) line);
                }

                displayInfo(new Recipe(name, id, imageUrls, totalTime, totalTimeInSeconds, rating, numOfServing, ingredientLines, source));
            }
            catch(ParseException pe) {
                System.out.println("position: " + pe.getPosition());
                System.out.println(pe);
            }
            catch(NullPointerException e){
                System.out.println("Null pointer");
            }
            displayInfo(null);
        }
    }

}
