package com.hemangnh18.chatmate.FCM;

import com.hemangnh18.chatmate.Classes.SocketMessage;

public class SenderBox {

    public SocketMessage data;
    public String to;

    public SenderBox(SocketMessage data, String to) {
        this.data = data;
        this.to = to;
    }

    public SenderBox() {
    }
}
