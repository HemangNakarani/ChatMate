package com.hemangnh18.chatmate.Classes;

public class ContactDb {

    String NAME,NUMBER,ID;

    public ContactDb() {
    }

    public String getNAME() {
        return NAME;
    }

    public ContactDb(String NAME, String NUMBER) {
        this.NAME = NAME;
        this.NUMBER = NUMBER;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getNUMBER() {
        return NUMBER;
    }

    public void setNUMBER(String NUMBER) {
        this.NUMBER = NUMBER;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public ContactDb(String NAME, String NUMBER, String ID) {
        this.NAME = NAME;
        this.NUMBER = NUMBER;
        this.ID = ID;
    }
}
