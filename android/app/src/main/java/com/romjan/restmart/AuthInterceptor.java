package com.romjan.restmart;

import android.content.Context;
import android.content.Intent;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private Context context;
    private SharedPreferencesManager sharedPreferencesManager;


    public AuthInterceptor(Context context) {
        this.context = context;
        this.sharedPreferencesManager = new SharedPreferencesManager(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if (response.code() == 401) {
            // Token expired, logout user
            logout();
        }

        return response;
    }

    private void logout() {
        // Clear SharedPreferences
        sharedPreferencesManager.clearLoginData();

        // Redirect to LoginActivity
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
