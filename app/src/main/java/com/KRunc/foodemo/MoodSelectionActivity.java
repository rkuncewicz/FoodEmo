package com.KRunc.foodemo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

// Container Activity must implement this interface
interface RequestCallback {
    public void setupRequest(int choice, int[] choices);
}


public class MoodSelectionActivity extends ActionBarActivity implements RequestCallback {

    private static final String ARG_SECTION_NUMBER = "section_number";

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    public int section;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println ("created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_selection);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mood_selection, menu);
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

    public void setupRequest(int choice, int[] choices){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        String keywords = "";
        switch (choices[choice]) {
            case R.drawable.anime:
                keywords = "cute";
                break;
            case R.drawable.ecstatic:
                keywords = "bacon";
                break;
            case R.drawable.sad:
                keywords = "macaroni+cheese";
                break;
            case R.drawable.short_on_time:
                keywords = "&maxTotalTimeInSeconds=900";
                break;
            case R.drawable.angry:
                keywords = "curry";
                break;
            case R.drawable.cupcake:
                keywords = "dessert";
                break;
            case R.drawable.fruit:
                keywords = "fruit";
                break;
            case R.drawable.love:
                keywords = "bacon+eggs";
                break;
            case R.drawable.party:
                keywords = "party";
                break;
            case R.drawable.vegetable:
                keywords = "vegetarian";
                break;
            case R.drawable.chocolate:
                keywords = "chocolate";
                break;
            case R.drawable.drink:
                keywords = "drink";
                break;
        }

        System.out.println(keywords);

        if (networkInfo != null && networkInfo.isConnected()) {
            DownloadWebpageTask task = new DownloadWebpageTask();
            task.execute(keywords);
        } else {
            //TODO: Pretty error handling
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

    /**
     * A FragmentStatePagerAdapter that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, position+1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment{
        RequestCallback callback;

        private int sectionNumber;

        private static final String ARG_SECTION_NUMBER = "section_number";

        public void onAttach(Activity activity) {
            super.onAttach(activity);
            try {
                callback = (RequestCallback) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
            }
        }


        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            sectionNumber = getArguments() != null ? getArguments().getInt(ARG_SECTION_NUMBER) : 0;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_mood_selection, container, false);

            setThumbnails(rootView, sectionNumber);

            return rootView;
        }

        public void setThumbnails (View view, int sectionNumber) {
            ImageButton imgButton1 = (ImageButton) view.findViewById(R.id.button1);
            ImageButton imgButton2 = (ImageButton) view.findViewById(R.id.button2);
            ImageButton imgButton3 = (ImageButton) view.findViewById(R.id.button3);
            ImageButton imgButton4 = (ImageButton) view.findViewById(R.id.button4);
            int[] choices = new int[4];
            switch (sectionNumber) {
                case 1:
                    imgButton1.setImageResource(R.drawable.anime);
                    imgButton2.setImageResource(R.drawable.ecstatic);
                    imgButton3.setImageResource(R.drawable.sad);
                    imgButton4.setImageResource(R.drawable.short_on_time);
                    choices[0] = R.drawable.anime;
                    choices[1] = R.drawable.ecstatic;
                    choices[2] = R.drawable.sad;
                    choices[3] = R.drawable.short_on_time;
                    break;
                case 2:
                    imgButton1.setImageResource(R.drawable.angry);
                    imgButton2.setImageResource(R.drawable.cupcake);
                    imgButton3.setImageResource(R.drawable.fruit);
                    imgButton4.setImageResource(R.drawable.love);
                    choices[0] = R.drawable.angry;
                    choices[1] = R.drawable.cupcake;
                    choices[2] = R.drawable.fruit;
                    choices[3] = R.drawable.love;
                    break;
                default:
                    imgButton1.setImageResource(R.drawable.party);
                    imgButton2.setImageResource(R.drawable.vegetable);
                    imgButton3.setImageResource(R.drawable.chocolate);
                    imgButton4.setImageResource(R.drawable.drink);
                    choices[0] = R.drawable.party;
                    choices[1] = R.drawable.vegetable;
                    choices[2] = R.drawable.chocolate;
                    choices[3] = R.drawable.drink;
            }

            imgButton1.setOnClickListener(new CustomClickListener(0, choices));
            imgButton2.setOnClickListener(new CustomClickListener(1, choices));
            imgButton3.setOnClickListener(new CustomClickListener(2, choices));
            imgButton4.setOnClickListener(new CustomClickListener(3, choices));
        }

        public class CustomClickListener implements View.OnClickListener
        {
            int choice;
            int[] choices;

            public CustomClickListener(int choice, int[] choices) {
                this.choice = choice;
                this.choices = choices;
            }

            @Override
            public void onClick(View pview)
            {
                callback.setupRequest (choice, choices);
            }

        };

    }
}
