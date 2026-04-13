package com.example.midtermsexam_beauty.views.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.midtermsexam_beauty.R;
import com.example.midtermsexam_beauty.utilities.SessionManager;
import com.example.midtermsexam_beauty.utilities.SupabaseAuthService;
import com.example.midtermsexam_beauty.views.seller.SellerDashboard;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLoginSubmit;
    private SupabaseAuthService authService;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_page);

        hideSystemUI();

        authService = new SupabaseAuthService();
        executor = Executors.newSingleThreadExecutor();

        etUsername = findViewById(R.id.usernameEditText);
        etPassword = findViewById(R.id.passwordEditText);
        btnLoginSubmit = findViewById(R.id.loginButton);
        etUsername.setHint("Email");

        btnLoginSubmit.setOnClickListener(v -> {
            String email = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!authService.isConfigured()) {
                Toast.makeText(this, "Supabase is not configured.", Toast.LENGTH_LONG).show();
            } else {
                btnLoginSubmit.setEnabled(false);
                executor.execute(() -> {
                    SupabaseAuthService.AuthResult result = authService.signIn(email, pass);
                    if (result.success) {
                        // Save session
                        SessionManager session = new SessionManager(LoginActivity.this);
                        session.saveSession(result.accessToken, result.userId);

                        // Check if seller
                        boolean seller = authService.isSeller(result.accessToken, result.userId);
                        session.setIsSeller(seller);

                        runOnUiThread(() -> {
                            btnLoginSubmit.setEnabled(true);
                            Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            Intent intent;
                            if (seller) {
                                intent = new Intent(LoginActivity.this, SellerDashboard.class);
                            } else {
                                intent = new Intent(LoginActivity.this, Homepage.class);
                            }
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> {
                            btnLoginSubmit.setEnabled(true);
                            Toast.makeText(LoginActivity.this, result.message, Toast.LENGTH_LONG).show();
                        });
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (executor != null) {
            executor.shutdown();
        }
        super.onDestroy();
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
}
