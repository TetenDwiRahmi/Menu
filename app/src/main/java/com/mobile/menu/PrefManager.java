package com.mobile.menu;

import android.content.Context;
import android.content.SharedPreferences;

import com.androidnetworking.interfaces.JSONObjectRequestListener;

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "data_app";

    public PrefManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setIdUser(String idUser){
        editor.putString("idUser", idUser);
        editor.apply();
    }
    public String getIdUser(){
        return pref.getString("idUser", "");
    }

    public void setLoginStatus(boolean islogin){
        editor.putBoolean("login", islogin);
        editor.apply();
    }

    public boolean getLoginStatus(){
        return pref.getBoolean("login", false);
    }
}
