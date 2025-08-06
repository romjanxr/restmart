package com.romjan.restmart;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.romjan.restmart.databinding.ActivitySignUpBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = NetworkConfig.getApiService();

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.btnSignUp.setOnClickListener(v -> {
            if (validateInputs()) {
                performRegistration();
            }
        });

        binding.tvLogin.setOnClickListener(v -> {
            navigateToLogin();
        });
    }

    private boolean validateInputs() {
        String firstName = binding.etFirstName.getText().toString().trim();
        String lastName = binding.etLastName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        String phoneNumber = binding.etPhoneNumber.getText().toString().trim();

        // Reset error states
        binding.tilFirstName.setError(null);
        binding.tilLastName.setError(null);
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);
        binding.tilAddress.setError(null);
        binding.tilPhoneNumber.setError(null);
        binding.tvError.setVisibility(View.GONE);

        if (TextUtils.isEmpty(firstName)) {
            binding.tilFirstName.setError("First Name is required");
            binding.etFirstName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(lastName)) {
            binding.tilLastName.setError("Last Name is required");
            binding.etLastName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            binding.tilEmail.setError("Email is required");
            binding.etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError("Please enter a valid email");
            binding.etEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError("Password is required");
            binding.etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            binding.tilPassword.setError("Password must be at least 6 characters");
            binding.etPassword.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(address)) {
            binding.tilAddress.setError("Address is required");
            binding.etAddress.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            binding.tilPhoneNumber.setError("Phone Number is required");
            binding.etPhoneNumber.requestFocus();
            return false;
        }

        return true;
    }

    private void performRegistration() {
        String firstName = binding.etFirstName.getText().toString().trim();
        String lastName = binding.etLastName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        String phoneNumber = binding.etPhoneNumber.getText().toString().trim();

        setLoadingState(true);

        RegisterRequest registerRequest = new RegisterRequest(email, password, firstName, lastName, address, phoneNumber);

        Call<RegisterResponse> call = apiService.register(registerRequest);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                setLoadingState(false);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(SignUpActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    navigateToLogin(); // Or navigate to MainActivity if auto-login after registration
                } else {
                    handleRegistrationError("Registration failed. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                setLoadingState(false);
                handleRegistrationError("Network error. Please check your connection and try again.");
            }
        });
    }

    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            binding.btnSignUp.setText("");
            binding.btnSignUp.setEnabled(false);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.btnSignUp.setText("SIGN UP");
            binding.btnSignUp.setEnabled(true);
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void handleRegistrationError(String errorMessage) {
        binding.tvError.setText(errorMessage);
        binding.tvError.setVisibility(View.VISIBLE);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}