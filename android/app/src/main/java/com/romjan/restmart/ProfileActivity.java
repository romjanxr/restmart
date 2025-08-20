package com.romjan.restmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.view.Window;
import android.view.WindowManager;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvFullName, tvEmail, tvAddress, tvPhoneNumber;
    private Button btnEditProfile;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvAddress = findViewById(R.id.tvAddress);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("user", currentUser);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchUserProfile();
    }

    private void fetchUserProfile() {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        findViewById(R.id.profileContainer).setVisibility(View.INVISIBLE);

        SharedPreferencesManager authPreferences = new SharedPreferencesManager(this);
        String authToken = "Bearer " + authPreferences.getAuthToken();

        ApiService apiService = NetworkConfig.getApiService(this);
        apiService.getUser(authToken).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    populateUI(currentUser);
                    findViewById(R.id.profileContainer).setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to fetch user profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                Toast.makeText(ProfileActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateUI(User user) {
        tvFullName.setText(user.getFirstName() + " " + user.getLastName());
        tvEmail.setText(user.getEmail());
        tvAddress.setText(user.getAddress());
        tvPhoneNumber.setText(user.getPhoneNumber());
    }
}