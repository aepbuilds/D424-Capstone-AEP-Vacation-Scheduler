package com.aep.vacationscheduler.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.aep.vacationscheduler.MainActivity;
import com.aep.vacationscheduler.R;
import com.aep.vacationscheduler.auth.AuthManager;

public class LoginActivity extends AppCompatActivity {
    private AuthManager authManager;
    private EditText etUsername, etPassword;
    private Button btnAction;
    private TextView tvStatus;
    private boolean registrationMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authManager = new AuthManager(this);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnAction = findViewById(R.id.btnAction);
        tvStatus = findViewById(R.id.tvStatus);

        registrationMode = !authManager.hasAccount();
        btnAction.setText(registrationMode ? "Register" : "Login");

        btnAction.setOnClickListener(v -> handleSubmit());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLockoutStatus();
    }

    private void updateLockoutStatus() {
        long remaining = authManager.getRemainingLockoutMillis();
        if (remaining > 0) {
            btnAction.setEnabled(false);
            tvStatus.setText("Too many failed attempts. Try again in " + (remaining / 1000) + "s.");
            tvStatus.postDelayed(this::updateLockoutStatus, 1000);
        } else {
            btnAction.setEnabled(true);
            tvStatus.setText("");
        }
    }

    private void handleSubmit() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter a username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (registrationMode) {
            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            authManager.registerAccount(username, password);
            authManager.login(username, password);
            goToMain();
            return;
        }

        if (authManager.getRemainingLockoutMillis() > 0) {
            updateLockoutStatus();
            return;
        }

        if (authManager.login(username, password)) {
            goToMain();
        } else if (authManager.getRemainingLockoutMillis() > 0) {
            updateLockoutStatus();
        } else {
            Toast.makeText(this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}