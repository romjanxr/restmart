package com.romjan.restmart;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static final String PREF_NAME = "RestMartPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_AUTH_TOKEN = "authToken";
    private static final String KEY_USER_FIRST_NAME = "userFirstName";
    private static final String KEY_USER_LAST_NAME = "userLastName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_CART_PK = "cartPk";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SharedPreferencesManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveLoginData(String token, String firstName, String lastName, String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.putString(KEY_USER_FIRST_NAME, firstName);
        editor.putString(KEY_USER_LAST_NAME, lastName);
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getAuthToken() {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null);
    }

    public String getUserFirstName() {
        return sharedPreferences.getString(KEY_USER_FIRST_NAME, "");
    }

    public String getUserLastName() {
        return sharedPreferences.getString(KEY_USER_LAST_NAME, "");
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }

    public void saveCartPk(String cartPk) {
        editor.putString(KEY_CART_PK, cartPk);
        editor.apply();
    }

    public String getCartPk() {
        return sharedPreferences.getString(KEY_CART_PK, null);
    }

    public void clearCartPk() {
        editor.remove(KEY_CART_PK).apply();
    }

    public void updateUserDetails(String firstName, String lastName, String email) {
        editor.putString(KEY_USER_FIRST_NAME, firstName);
        editor.putString(KEY_USER_LAST_NAME, lastName);
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    public void clearLoginData() {
        editor.clear();
        editor.apply();
    }
}
