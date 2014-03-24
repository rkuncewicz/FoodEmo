package com.KRunc.foodemo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robert Kuncewicz on 26/02/14.
 * FoodEmo Recipe Application
 */
public class Recipe implements Parcelable {
    private String name;
    private String id;
    private String[] pictureUrls;
    private String totalTime;
    private long totalTimeInSeconds;
    private long rating;
    private long numOfServing;
    private String[] ingredientLines;
    private String source;

    public Recipe(String name, String id, ArrayList<String> pictureUrls){
        this.name = name;
        this.id = id;
        this.pictureUrls = pictureUrls.toArray(new String[pictureUrls.size()]);
    }

    public Recipe (String name, String id, ArrayList<String> pictureUrls, String totalTime, long totalTimeInSeconds,
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

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    private Recipe(Parcel in) {
        name = in.readString();
        id = in.readString();
        pictureUrls = in.createStringArray();
    }
}
