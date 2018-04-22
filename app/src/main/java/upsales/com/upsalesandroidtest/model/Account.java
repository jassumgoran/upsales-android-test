package upsales.com.upsalesandroidtest.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Goran on 20.4.2018.
 */

public class Account implements Parcelable {

    public static final String TAG = "Account";

    String name;
    List<User> users;
    List<Address> addresses;

    public String getAccountManager(){
        return users != null && users.size() > 0 ? users.get(0).getName() : "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }



    protected Account(Parcel in) {
        name = in.readString();
        if (in.readByte() == 0x01) {
            users = new ArrayList<User>();
            in.readList(users, User.class.getClassLoader());
        } else {
            users = null;
        }
        if (in.readByte() == 0x01) {
            addresses = new ArrayList<Address>();
            in.readList(addresses, Address.class.getClassLoader());
        } else {
            addresses = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        if (users == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(users);
        }
        if (addresses == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(addresses);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };
}