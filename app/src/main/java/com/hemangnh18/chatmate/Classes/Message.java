package com.hemangnh18.chatmate.Classes;

public class Message {
    private String Message;
    private String Sender;
    private String Reciever;
    private String Time;
    private String Room;
    private String Type;

    public Message() {}

    public Message(String message, String sender, String reciever, String time, String room, String type) {
        Message = message;
        Sender = sender;
        Reciever = reciever;
        Time = time;
        Room = room;
        Type = type;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public String getReciever() {
        return Reciever;
    }

    public void setReciever(String reciever) {
        Reciever = reciever;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getRoom() {
        return Room;
    }

    public void setRoom(String room) {
        Room = room;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    @Override
    public String toString() {
        return "Message{" +
                "Message='" + Message + '\'' +
                ", Sender='" + Sender + '\'' +
                ", Reciever='" + Reciever + '\'' +
                ", Time='" + Time + '\'' +
                ", Room='" + Room + '\'' +
                ", Type='" + Type + '\'' +
                '}';
    }
}
