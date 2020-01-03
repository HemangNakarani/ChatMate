package com.hemangnh18.chatmate.Threading;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class FetchCurrentChatsFactory implements ViewModelProvider.Factory {
    private Context context;

    public FetchCurrentChatsFactory(Context context) {
        this.context=context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new FetchCurrentChats(context);
    }
}
