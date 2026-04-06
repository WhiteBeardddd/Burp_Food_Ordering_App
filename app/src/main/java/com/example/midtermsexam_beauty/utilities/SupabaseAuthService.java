package com.example.midtermsexam_beauty.utilities;

import com.example.midtermsexam_beauty.BuildConfig;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SupabaseAuthService {

    public static class AuthResult {
        public final boolean success;
        public final String message;
        public final String accessToken;

        public AuthResult(boolean success, String message, String accessToken) {
            this.success = success;
            this.message = message;
            this.accessToken = accessToken;
        }
    }

    public boolean isConfigured() {
        return !BuildConfig.SUPABASE_URL.isBlank() && !BuildConfig.SUPABASE_ANON_KEY.isBlank();
    }

    public AuthResult signUp(String email, String password, String username) {
        try {
            JSONObject payload = new JSONObject()
                    .put("email", email)
                    .put("password", password)
                    .put("data", new JSONObject().put("username", username));

            HttpResponse response = post("/auth/v1/signup", payload.toString());
            if (response.statusCode >= 200 && response.statusCode < 300) {
                return new AuthResult(true, "Registration successful. Please verify your email if required.", null);
            }
            return new AuthResult(false, extractErrorMessage(response.body), null);
        } catch (Exception ex) {
            return new AuthResult(false, "Registration failed: " + ex.getMessage(), null);
        }
    }

    public AuthResult signIn(String email, String password) {
        try {
            JSONObject payload = new JSONObject()
                    .put("email", email)
                    .put("password", password);

            HttpResponse response = post("/auth/v1/token?grant_type=password", payload.toString());
            if (response.statusCode >= 200 && response.statusCode < 300) {
                String token = tryExtractField(response.body, "access_token");
                return new AuthResult(true, "Login successful.", token);
            }
            return new AuthResult(false, extractErrorMessage(response.body), null);
        } catch (Exception ex) {
            return new AuthResult(false, "Login failed: " + ex.getMessage(), null);
        }
    }

    private HttpResponse post(String path, String body) throws IOException {
        String baseUrl = BuildConfig.SUPABASE_URL.endsWith("/")
                ? BuildConfig.SUPABASE_URL.substring(0, BuildConfig.SUPABASE_URL.length() - 1)
                : BuildConfig.SUPABASE_URL;

        URL url = new URL(baseUrl + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("apikey", BuildConfig.SUPABASE_ANON_KEY);
        conn.setRequestProperty("Authorization", "Bearer " + BuildConfig.SUPABASE_ANON_KEY);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            os.write(bytes, 0, bytes.length);
        }

        int statusCode = conn.getResponseCode();
        InputStream stream = (statusCode >= 200 && statusCode < 300) ? conn.getInputStream() : conn.getErrorStream();
        String responseBody = stream != null ? readAll(stream) : "";
        conn.disconnect();
        return new HttpResponse(statusCode, responseBody);
    }

    private static String readAll(InputStream inputStream) throws IOException {
        StringBuilder out = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
        }
        return out.toString();
    }

    private String extractErrorMessage(String body) {
        try {
            JSONObject json = new JSONObject(body);
            if (json.has("msg")) {
                return json.getString("msg");
            }
            if (json.has("error_description")) {
                return json.getString("error_description");
            }
            if (json.has("message")) {
                return json.getString("message");
            }
            if (json.has("error")) {
                return json.getString("error");
            }
        } catch (JSONException ignored) {
            return body == null || body.isBlank() ? "Authentication request failed." : body;
        }
        return "Authentication request failed.";
    }

    private String tryExtractField(String body, String field) {
        try {
            JSONObject json = new JSONObject(body);
            if (json.has(field)) {
                return json.getString(field);
            }
        } catch (JSONException ignored) {
            return null;
        }
        return null;
    }

    private static class HttpResponse {
        final int statusCode;
        final String body;

        HttpResponse(int statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body;
        }
    }
}
