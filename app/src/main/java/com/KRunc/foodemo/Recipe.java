package com.KRunc.foodemo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Robert Kuncewicz on 26/02/14.
 * FoodEmo Recipe Application
 */
public class Recipe implements Parcelable {
    private String name;
    private List<String> pictureUrls;

    public Recipe(String name, List<String> pictureUrls){
        this.name = name;
    }

    public String getName() { return name; }
    public List<String> getPictureUrls() { return pictureUrls; }

    public void setName(String name) { this.name = name; }
    public void setPictureUrl(List<String> pictureUrls) { this.pictureUrls = pictureUrls; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeList(pictureUrls);
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
        in.readList(pictureUrls, null);
    }
}
