package com.KRunc.foodemo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import Interfaces.Recipe;

/**
 * Created by Robert Kuncewicz on 26/02/14.
 * FoodEmo Recipe Application
 */
public class YummlyRecipe implements Recipe {
    private final String name;
    private final String id;
    private String[] pictureUrls;
    private String totalTime = null;
    private long totalTimeInSeconds = 0L;
    private long rating = 0L;
    private long numOfServing = 0L;
    private String[] ingredientLines = null;
    private String source = null;

    public YummlyRecipe(String name, String id, ArrayList<String> pictureUrls){
        this.name = name;
        this.id = id;
        this.pictureUrls = pictureUrls.toArray(new String[pictureUrls.size()]);
    }

    public YummlyRecipe (String name, String id, ArrayList<String> pictureUrls, String totalTime, long totalTimeInSeconds,
                        long rating, long numOfServing, ArrayList<String> ingredientLines, String source){
        this.name = name;
        this.id = id;
        this.pictureUrls = pictureUrls.toArray(new String[pictureUrls.size()]);
        this.totalTime = totalTime;
        this.totalTimeInSeconds = totalTimeInSeconds;
        this.rating = rating;
        this.numOfServing = numOfServing;
        this.ingredientLines = ingredientLines.toArray(new String[ingredientLines.size()]);
        this.source = source;
    }

    public String getName() { return name; }
    public String getId() { return id; }
    public String[] getPictureUrls() { return pictureUrls; }
    public String[] getIngredientLines() { return ingredientLines; }
    public String getTotalTime() { return totalTime; }

    public void setPictureUrls(String[] pictureUrls) {
        this.pictureUrls = pictureUrls;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(id);
        parcel.writeStringArray(pictureUrls);
    }

    public static final Parcelable.Creator<YummlyRecipe> CREATOR = new Parcelable.Creator<YummlyRecipe>() {
        public YummlyRecipe createFromParcel(Parcel in) {
            return new YummlyRecipe(in);
        }

        public YummlyRecipe[] newArray(int size) {
            return new YummlyRecipe[size];
        }
    };

    private YummlyRecipe(Parcel in) {
        name = in.readString();
        id = in.readString();
        pictureUrls = in.createStringArray();
    }
}
