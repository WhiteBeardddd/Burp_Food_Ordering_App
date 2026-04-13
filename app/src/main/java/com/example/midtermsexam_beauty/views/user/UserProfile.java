package com.example.midtermsexam_beauty.views.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.midtermsexam_beauty.R;
import com.example.midtermsexam_beauty.models.Profile;
import com.example.midtermsexam_beauty.utilities.SessionManager;
import com.example.midtermsexam_beauty.utilities.SupabaseAuthService;
import com.example.midtermsexam_beauty.views.seller.SellerDashboard;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserProfile extends AppCompatActivity {

    private EditText etFullName, etPhone;
    private SwitchMaterial switchIsSeller;
    private Button btnSave;
    private SessionManager session;
    private SupabaseAuthService authService;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        session = new SessionManager(this);
        authService = new SupabaseAuthService();
        executor = Executors.newSingleThreadExecutor();

        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        switchIsSeller = findViewById(R.id.switchIsSeller);
        btnSave = findViewById(R.id.btnSaveProfile);

        loadProfile();

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadProfile() {
        executor.execute(() -> {
            Profile profile = authService.getProfile(
                    session.getToken(),
                    session.getUserId()
            );
            if (profile != null) {
                runOnUiThread(() -> {
                    etFullName.setText(profile.getFullName());
                    etPhone.setText(profile.getPhone());
                    switchIsSeller.setChecked(profile.isSeller());
                });
            }
        });
    }

    private void saveProfile() {
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        boolean isSeller = switchIsSeller.isChecked();

        btnSave.setEnabled(false);
        executor.execute(() -> {
            boolean success = authService.updateProfile(
                    session.getToken(),
                    session.getUserId(),
                    fullName,
                    phone,
                    isSeller
            );

            session.setIsSeller(isSeller);

            runOnUiThread(() -> {
                btnSave.setEnabled(true);
                if (success) {
                    Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
                    if (isSeller) {
                        startActivity(new Intent(this, SellerDashboard.class));
                    } else {
                        startActivity(new Intent(this, Homepage.class));
                    }
                    finish();
                } else {
                    Toast.makeText(this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        if (executor != null) executor.shutdown();
        super.onDestroy();
    }
}