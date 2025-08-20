package com.romjan.restmart;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.view.Window;
import android.view.WindowManager;

public class SettingsActivity extends AppCompatActivity {

    private EditText etCurrentPassword, etNewPassword, etConfirmNewPassword;
    private Button btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void changePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmNewPassword = etConfirmNewPassword.getText().toString().trim();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isPasswordValid(newPassword)) {
            Toast.makeText(this, "Password must be at least 8 characters long, contain at least one special character, one uppercase letter, and one lowercase letter.", Toast.LENGTH_LONG).show();
            return;
        }

        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        findViewById(R.id.settingsContainer).setVisibility(View.INVISIBLE);

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(newPassword, currentPassword);

        SharedPreferencesManager authPreferences = new SharedPreferencesManager(this);
        String authToken = "Bearer " + authPreferences.getAuthToken();

        ApiService apiService = NetworkConfig.getApiService(this);
        apiService.changePassword(authToken, changePasswordRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                findViewById(R.id.settingsContainer).setVisibility(View.VISIBLE);
                if (response.isSuccessful()) {
                    Toast.makeText(SettingsActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(SettingsActivity.this, "Failed to change password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                findViewById(R.id.settingsContainer).setVisibility(View.VISIBLE);
                Toast.makeText(SettingsActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isPasswordValid(String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
