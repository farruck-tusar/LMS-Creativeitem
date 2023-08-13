package com.tusar.creativeitem.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
/**
 * Created by Farruck Ahmed Tusar on 11-Jul-17.
 */

public class SessionManager  {

    public static final String TAG = SessionManager.class.getSimpleName();

    SharedPreferences sharedPreferences;
    Editor editor;
    Context context;

    //Shared pref mode
    int PRIVATE_Mode= 0;

    // Shared preferences file name
    public static final String PREF_Name = "creativeItem";
    public static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    public SessionManager(Context context){
        this.context = context;
        sharedPreferences= context.getSharedPreferences(PREF_Name, PRIVATE_Mode);
        editor = sharedPreferences.edit();
    }

    public void setLogIn(boolean isLoggedIn){
        editor.putBoolean(KEY_IS_LOGGEDIN,isLoggedIn);

        // commit changes
        editor.commit();
        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn(){
        return sharedPreferences.getBoolean(KEY_IS_LOGGEDIN,false);
    }
}
