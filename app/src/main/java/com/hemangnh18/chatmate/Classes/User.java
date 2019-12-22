package com.hemangnh18.chatmate.Classes;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String USERNAME;
    private String STATUS;
    private String USER_ID;
    private String GENDER;
    private String DP;

    public User(String USERNAME, String STATUS, String USER_ID, String GENDER, String DP) {
        this.USERNAME = USERNAME;
        this.STATUS = STATUS;
        this.USER_ID = USER_ID;
        this.GENDER = GENDER;
        this.DP = DP;
    }

    public User() {
    }

    protected User(Parcel in) {
        USERNAME = in.readString();
        STATUS = in.readString();
        USER_ID = in.readString();
        GENDER = in.readString();
        DP = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(USERNAME);
        dest.writeString(STATUS);
        dest.writeString(USER_ID);
        dest.writeString(GENDER);
        dest.writeString(DP);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getGENDER() {
        return GENDER;
    }

    public void setGENDER(String GENDER) {
        this.GENDER = GENDER;
    }

    public String getDP() {
        return DP;
    }

    public void setDP(String DP) {
        this.DP = DP;
    }

    @Override
    public String toString() {
        return "User{" +
                "USERNAME='" + USERNAME + '\'' +
                ", STATUS='" + STATUS + '\'' +
                ", USER_ID='" + USER_ID + '\'' +
                ", GENDER='" + GENDER + '\'' +
                ", DP='" + DP + '\'' +
                '}';
    }
}
