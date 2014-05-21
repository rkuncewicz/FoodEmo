package Interfaces;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by certeis on 21/05/14.
 */
public interface Recipe extends Parcelable {
    public String getName();
    public String getId();
    public String[] getPictureUrls();
    public String[] getIngredientLines();
    public String getTotalTime();
    public void setPictureUrls(String[] pictureUrls);
    public int describeContents();
    public void writeToParcel(Parcel parcel, int i);
}
