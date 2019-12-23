package com.hemangnh18.chatmate;



import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.hemangnh18.chatmate.Classes.User;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private SharedPreferences preferences;
    private User user;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        preferences = getContext().getSharedPreferences("UserInfo",MODE_PRIVATE);
        Gson gson = new Gson();
        user = gson.fromJson(preferences.getString("User",""),User.class);
        Toast.makeText(getContext(),user.toString(),Toast.LENGTH_LONG).show();
        TextView txt = view.findViewById(R.id.txto);
        txt.setText(user.toString());

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public static HomeFragment newInstance() {
        HomeFragment homeFragment = new HomeFragment();
        return homeFragment;
    }
}
