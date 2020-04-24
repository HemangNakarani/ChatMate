package com.hemangnh18.chatmate.Socket;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.hemangnh18.chatmate.Classes.SocketMessage;
import com.hemangnh18.chatmate.Database.ChatMessagesHandler;
import com.hemangnh18.chatmate.MessageActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class SocketMethods
{

    private Thread thread2;
    private boolean startTyping = false;
    private int time = 2;
    private final Handler handler;
    private Context context;

    public SocketMethods(Context context) {

        this.context = context;
        handler = new Handler(context.getMainLooper());
    }


    @SuppressLint("HandlerLeak")
    Handler handler2=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(time == 0){

                //TODO SEND EVENTBUS TO STOP TYPING;
                startTyping = false;
                time = 2;
            }

        }
    };

    private void runOnUiThread(Runnable r) {
        handler.post(r);
    }

    public Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONObject data = (JSONObject) args[0];

                    try {

                        String message = data.getString("message");
                        Log.e("In Activity", message);
                        SocketMessage pojo = new SocketMessage();
                        pojo.setMessage(data.getString("message"));
                        pojo.setReciever(data.getString("reciever"));
                        pojo.setSender(data.getString("sender"));
                        pojo.setTime(data.getString("time"));
                        pojo.setType(data.getString("type"));
                        pojo.setRoom(data.getString("sender"));

                        FirebaseDatabase.getInstance().getReference("MsgStatus").child(pojo.getSender()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("Delivered");

                        ChatMessagesHandler chatMessagesHandler = new ChatMessagesHandler(context);
                        chatMessagesHandler.addMessage(pojo);

                        EventBus.getDefault().post(pojo);

                    } catch (Exception e) {
                        return;
                    }
                }
            });
        }
    };


    public Emitter.Listener onNewUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int length = args.length;

                    if(length == 0){
                        return;
                    }

                    String username =args[0].toString();
                    try {
                        JSONObject object = new JSONObject(username);
                        username = object.getString("username");
                       //String id = object.getString("uid");
                        //TODO NOTIFY IF USER IS FRIEND


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.e("In Activity Joined",username);

                }
            });
        }
    };


    public Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];

                    try {
                        Boolean typingOrNot = data.getBoolean("typing");
                        String sender = data.getString("sender") + " is Typing......";

                        //TODO SEND EVENTBUS TO SET STATUS,TYPING...
                        Log.e("TYYYYPPP",sender);

                        if(typingOrNot){

                            if(!startTyping){
                                startTyping = true;
                                thread2=new Thread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                while(time > 0) {
                                                    synchronized (this){
                                                        try {
                                                            wait(1000);
                                                            Log.e(TAG, "run: typing " + time);
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                        time--;
                                                    }
                                                    handler2.sendEmptyMessage(0);
                                                }

                                            }
                                        }
                                );
                                thread2.start();
                            }else {
                                time = 2;
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


    public Emitter.Listener Establish = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int length = args.length;

                    if(length == 0){
                        return;
                    }
                    FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

                    String mesg =args[0].toString();
                    Log.e("In Activity ReJoined",mesg);
                    SocketHandler.getSocket().emit("create",fuser.getUid());

                }
            });
        }
    };

}
