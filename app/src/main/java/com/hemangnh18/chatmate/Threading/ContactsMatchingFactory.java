package com.hemangnh18.chatmate.Threading;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ContactsMatchingFactory implements ViewModelProvider.Factory {

    private Context context;

    public ContactsMatchingFactory(Context context) {
        this.context=context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ContactsMatching(context);
    }
}
