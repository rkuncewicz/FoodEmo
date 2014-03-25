package com.KRunc.foodemo;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecipeListActivity extends ActionBarActivity {
    private ArrayList<Recipe> recipes;
    private ImageAdapter imgAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        recipes = getIntent().getParcelableArrayListExtra("recipes");

        ArrayList<String> recipePictures = new ArrayList<String>();
        for (int i = 0; i < recipes.size(); i++){
            Recipe recipe = recipes.get(i);
            String[] urls = recipe.getPictureUrls();
            for (int j = 0; j < urls.length; j++) {
                urls[j] = urls[j].replace(".s.png",".l.png");
                urls[j] = urls[j].replace(".s.jpg", ".l.jpg");
                urls[j] = urls[j].replace("=s90", "=s300");
            }
            recipe.setPictureUrls(urls);
            recipes.set(i, recipe);
        }

        GridView gridview = (GridView) findViewById(R.id.gridview);
        imgAdapter = new ImageAdapter(this, recipes);
        gridview.setAdapter(imgAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(RecipeListActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                forwardToRecipeDesc(position);
            }
        });
    }

    public void forwardToRecipeDesc (int id) {
        Recipe recipe = recipes.get(id);

        Intent intent = new Intent(this, RecipeDescriptionActivity.class);
        intent.putExtra("recipe", recipe);
        intent.putExtra("recipeImage", imgAdapter.getBitmapFromMemCache(recipe.getName()));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.recipe_list, menu);
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

}

