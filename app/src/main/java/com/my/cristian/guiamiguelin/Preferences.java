package com.my.cristian.guiamiguelin;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Cristian on 15/05/2017.
 */

public class Preferences {

    public static final String STRING_PREFERENCES = "guiamiguelin.cristian.tfg";
    public static final String PREFERENCE_USER_LOGIN = "preference.user.login";
    public static final String PREFERENCE_USER_NICK = "preference.user.nick";
    public static final String PREFERENCE_USER_FULL_NAME = "preference.user.full.name";

    public static void savePreferenceString(Context c, String b, String key){
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES,c.MODE_PRIVATE);
        preferences.edit().putString(key,b).apply();
    }

    public static String obtenerPreferenceString(Context c,String key){
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES,c.MODE_PRIVATE);
        return preferences.getString(key,"");//Si es que nunca se ha guardado nada en esta key pues retornara una cadena vacia
    }
}
