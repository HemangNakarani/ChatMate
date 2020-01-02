package com.hemangnh18.chatmate.Threading;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


public class FetchMessagesFactory implements ViewModelProvider.Factory {

    private Context context;
    private String id;

    public FetchMessagesFactory(Context context,String id) {
        this.context=context;
        this.id = id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new FetchMessages(context,id);
    }
}
