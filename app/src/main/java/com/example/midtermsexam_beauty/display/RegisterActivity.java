package com.example.midtermsexam_beauty.display;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.midtermsexam_beauty.R;
import com.example.midtermsexam_beauty.utilities.SupabaseAuthService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private TextView tvBackToLogin;
    private SupabaseAuthService authService;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        hideSystemUI();

        authService = new SupabaseAuthService();
        executor = Executors.newSingleThreadExecutor();

        etUsername = findViewById(R.id.regUsernameEditText);
        etEmail = findViewById(R.id.regEmailEditText);
        etPassword = findViewById(R.id.regPasswordEditText);
        etConfirmPassword = findViewById(R.id.regConfirmPasswordEditText);
        btnSignUp = findViewById(R.id.registerButton);
        tvBackToLogin = findViewById(R.id.backToLoginTextView);

        btnSignUp.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            String confirmPass = etConfirmPassword.getText().toString().trim();

            if (user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!pass.equals(confirmPass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (!authService.isConfigured()) {
                Toast.makeText(this, "Supabase is not configured. Add SUPABASE_URL and SUPABASE_ANON_KEY.", Toast.LENGTH_LONG).show();
            } else {
                btnSignUp.setEnabled(false);
                executor.execute(() -> {
                    SupabaseAuthService.AuthResult result = authService.signUp(email, pass, user);
                    runOnUiThread(() -> {
                        btnSignUp.setEnabled(true);
                        if (result.success) {
                            Toast.makeText(this, result.message, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        } else {
                            Toast.makeText(this, result.message, Toast.LENGTH_LONG).show();
                        }
                    });
                });
            }
        });

        tvBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    protected void onDestroy() {
        if (executor != null) {
            executor.shutdown();
        }
        super.onDestroy();
    }
}
