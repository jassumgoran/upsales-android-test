package upsales.com.upsalesandroidtest.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Goran on 21.4.2018.
 */

public class Filter implements Parcelable {

    public static final String TAG = "Filter";
    public static final int TYPE_ACCOUNT_MANAGERS = 1;

    //default "Any"
    int type;
    String label;
    List<Integer> userIds;

    public Filter(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Integer> userIds) {
        this.userIds = userIds;
    }

    protected Filter(Parcel in) {
        type = in.readInt();
        label = in.readString();
        if (in.readByte() == 0x01) {
            userIds = new ArrayList<Integer>();
            in.readList(userIds, Integer.class.getClassLoader());
        } else {
            userIds = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(label);
        if (userIds == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(userIds);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Filter> CREATOR = new Parcelable.Creator<Filter>() {
        @Override
        public Filter createFromParcel(Parcel in) {
            return new Filter(in);
        }

        @Override
        public Filter[] newArray(int size) {
            return new Filter[size];
        }
    };
}