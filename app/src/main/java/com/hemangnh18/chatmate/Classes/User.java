package com.hemangnh18.chatmate.Classes;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String USERNAME;
    private String USERNAME_IN_PHONE;
    private String PHONE;
    private String STATUS;
    private String USER_ID;
    private String GENDER;
    private String DP;
    private String DOWNLOAD;
    private String BASE64;
    private String TOKEN;

    protected User(Parcel in) {
        USERNAME = in.readString();
        USERNAME_IN_PHONE = in.readString();
        PHONE = in.readString();
        STATUS = in.readString();
        USER_ID = in.readString();
        GENDER = in.readString();
        DP = in.readString();
        DOWNLOAD = in.readString();
        BASE64 = in.readString();
        TOKEN = in.readString();
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

    public String getBASE64() {
        return BASE64;
    }

    public void setBASE64(String BASE64) {
        this.BASE64 = BASE64;
    }

    public User(String USERNAME, String USERNAME_IN_PHONE, String PHONE, String STATUS, String USER_ID, String GENDER, String DP, String DOWNLOAD, String BASE64, String TOKEN) {
        this.USERNAME = USERNAME;
        this.USERNAME_IN_PHONE = USERNAME_IN_PHONE;
        this.PHONE = PHONE;
        this.STATUS = STATUS;
        this.USER_ID = USER_ID;
        this.GENDER = GENDER;
        this.DP = DP;
        this.DOWNLOAD = DOWNLOAD;
        this.BASE64 = BASE64;
        this.TOKEN = TOKEN;
    }


    public User(String USERNAME, String PHONE, String STATUS, String USER_ID, String GENDER, String DP, String DOWNLOAD, String BASE64, String TOKEN) {
        this.USERNAME = USERNAME;
        this.PHONE = PHONE;
        this.STATUS = STATUS;
        this.USER_ID = USER_ID;
        this.GENDER = GENDER;
        this.DP = DP;
        this.DOWNLOAD = DOWNLOAD;
        this.BASE64 = BASE64;
        this.TOKEN = TOKEN;
    }


    public User() {
        this.USERNAME = "User";
        this.STATUS = "Hey People! ChatMate is Cool.";
        this.GENDER = "Not Provided";
        this.DP = "Default";
        this.DOWNLOAD = "Default";
        this.BASE64 = "Default";
        this.TOKEN = "";
        this.USERNAME_IN_PHONE = "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(USERNAME);
        parcel.writeString(USERNAME_IN_PHONE);
        parcel.writeString(PHONE);
        parcel.writeString(STATUS);
        parcel.writeString(USER_ID);
        parcel.writeString(GENDER);
        parcel.writeString(DP);
        parcel.writeString(DOWNLOAD);
        parcel.writeString(BASE64);
        parcel.writeString(TOKEN);
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getUSERNAME_IN_PHONE() {
        return USERNAME_IN_PHONE;
    }

    public void setUSERNAME_IN_PHONE(String USERNAME_IN_PHONE) {
        this.USERNAME_IN_PHONE = USERNAME_IN_PHONE;
    }

    public String getPHONE() {
        return PHONE;
    }

    public void setPHONE(String PHONE) {
        this.PHONE = PHONE;
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

    public String getDOWNLOAD() {
        return DOWNLOAD;
    }

    public void setDOWNLOAD(String DOWNLOAD) {
        this.DOWNLOAD = DOWNLOAD;
    }

    public String getTOKEN() {
        return TOKEN;
    }

    public void setTOKEN(String TOKEN) {
        this.TOKEN = TOKEN;
    }

    public static Creator<User> getCREATOR() {
        return CREATOR;
    }
}