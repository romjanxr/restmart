package com.romjan.restmart;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.view.Window;
import android.view.WindowManager;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etAddress, etPhoneNumber;
    private Button btnSave;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etAddress = findViewById(R.id.etAddress);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnSave = findViewById(R.id.btnSave);

        currentUser = (User) getIntent().getSerializableExtra("user");
        if (currentUser != null) {
            populateUI(currentUser);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void populateUI(User user) {
        etFirstName.setText(user.getFirstName());
        etLastName.setText(user.getLastName());
        etAddress.setText(user.getAddress());
        etPhoneNumber.setText(user.getPhoneNumber());
    }

    private void updateUserProfile() {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        findViewById(R.id.editProfileContainer).setVisibility(View.INVISIBLE);

        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();

        UpdateUserRequest updateUserRequest = new UpdateUserRequest(firstName, lastName, address, phoneNumber);

        SharedPreferencesManager authPreferences = new SharedPreferencesManager(this);
        String authToken = "Bearer " + authPreferences.getAuthToken();

        ApiService apiService = NetworkConfig.getApiService(this);
        apiService.updateUser(authToken, updateUserRequest).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                findViewById(R.id.editProfileContainer).setVisibility(View.VISIBLE);
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                findViewById(R.id.editProfileContainer).setVisibility(View.VISIBLE);
                Toast.makeText(EditProfileActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}