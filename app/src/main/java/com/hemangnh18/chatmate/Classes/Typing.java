package com.hemangnh18.chatmate.Classes;

public class Typing {
    String sender;
    boolean typing;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public boolean isTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }

    public Typing(String sender, boolean typing) {
        this.sender = sender;
        this.typing = typing;
    }
}
