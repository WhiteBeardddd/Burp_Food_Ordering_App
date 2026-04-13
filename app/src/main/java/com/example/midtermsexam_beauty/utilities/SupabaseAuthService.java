package com.example.midtermsexam_beauty.utilities;

import com.example.midtermsexam_beauty.BuildConfig;
import com.example.midtermsexam_beauty.models.Profile;
import org.json.JSONArray;
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
        public final String userId;

        public AuthResult(boolean success, String message, String accessToken, String userId) {
            this.success = success;
            this.message = message;
            this.accessToken = accessToken;
            this.userId = userId;
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
                return new AuthResult(true, "Registration successful. Please verify your email if required.", null, null);
            }
            return new AuthResult(false, extractErrorMessage(response.body), null, null);
        } catch (Exception ex) {
            return new AuthResult(false, "Registration failed: " + ex.getMessage(), null, null);
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
                String userId = null;
                try {
                    JSONObject json = new JSONObject(response.body);
                    if (json.has("user")) {
                        userId = json.getJSONObject("user").getString("id");
                    }
                } catch (Exception ignored) {}
                return new AuthResult(true, "Login successful.", token, userId);
            }
            return new AuthResult(false, extractErrorMessage(response.body), null, null);
        } catch (Exception ex) {
            return new AuthResult(false, "Login failed: " + ex.getMessage(), null, null);
        }
    }

    public Profile getProfile(String token, String authId) {
        try {
            String baseUrl = BuildConfig.SUPABASE_URL.endsWith("/")
                    ? BuildConfig.SUPABASE_URL.substring(0, BuildConfig.SUPABASE_URL.length() - 1)
                    : BuildConfig.SUPABASE_URL;

            URL url = new URL(baseUrl + "/rest/v1/profile?auth_id=eq." + authId + "&select=*");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setRequestProperty("apikey", BuildConfig.SUPABASE_ANON_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Accept", "application/json");

            int statusCode = conn.getResponseCode();
            InputStream stream = (statusCode >= 200 && statusCode < 300)
                    ? conn.getInputStream() : conn.getErrorStream();
            String body = stream != null ? readAll(stream) : "";
            conn.disconnect();

            if (statusCode >= 200 && statusCode < 300) {
                JSONArray arr = new JSONArray(body);
                if (arr.length() > 0) {
                    JSONObject obj = arr.getJSONObject(0);
                    Profile profile = new Profile();
                    profile.setId(obj.optString("id"));
                    profile.setAuthId(obj.optString("auth_id"));
                    profile.setFullName(obj.optString("full_name"));
                    profile.setSeller(obj.optBoolean("is_seller", false));
                    profile.setPhone(obj.optString("phone"));
                    profile.setAvatarUrl(obj.optString("avatar_url"));
                    return profile;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public boolean updateProfile(String token, String authId, String fullName, String phone, boolean isSeller) {
        try {
            JSONObject payload = new JSONObject();
            if (fullName != null) payload.put("full_name", fullName);
            if (phone != null) payload.put("phone", phone);
            payload.put("is_seller", isSeller);

            String baseUrl = BuildConfig.SUPABASE_URL.endsWith("/")
                    ? BuildConfig.SUPABASE_URL.substring(0, BuildConfig.SUPABASE_URL.length() - 1)
                    : BuildConfig.SUPABASE_URL;

            URL url = new URL(baseUrl + "/rest/v1/profile?auth_id=eq." + authId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PATCH");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setDoOutput(true);
            conn.setRequestProperty("apikey", BuildConfig.SUPABASE_ANON_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Prefer", "return=representation");

            try (OutputStream os = conn.getOutputStream()) {
                byte[] bytes = payload.toString().getBytes(StandardCharsets.UTF_8);
                os.write(bytes, 0, bytes.length);
            }

            int statusCode = conn.getResponseCode();
            conn.disconnect();
            return statusCode >= 200 && statusCode < 300;
        } catch (Exception ignored) {}
        return false;
    }

    public boolean isSeller(String token, String authId) {
        try {
            String baseUrl = BuildConfig.SUPABASE_URL.endsWith("/")
                    ? BuildConfig.SUPABASE_URL.substring(0, BuildConfig.SUPABASE_URL.length() - 1)
                    : BuildConfig.SUPABASE_URL;

            URL url = new URL(baseUrl + "/rest/v1/profile?auth_id=eq." + authId + "&select=is_seller");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setRequestProperty("apikey", BuildConfig.SUPABASE_ANON_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Accept", "application/json");

            int statusCode = conn.getResponseCode();
            InputStream stream = (statusCode >= 200 && statusCode < 300)
                    ? conn.getInputStream() : conn.getErrorStream();
            String body = stream != null ? readAll(stream) : "";
            conn.disconnect();

            if (statusCode >= 200 && statusCode < 300) {
                JSONArray arr = new JSONArray(body);
                if (arr.length() > 0) {
                    return arr.getJSONObject(0).getBoolean("is_seller");
                }
            }
        } catch (Exception ignored) {}
        return false;
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
        InputStream stream = (statusCode >= 200 && statusCode < 300)
                ? conn.getInputStream() : conn.getErrorStream();
        String responseBody = stream != null ? readAll(stream) : "";
        conn.disconnect();
        return new HttpResponse(statusCode, responseBody);
    }

    private static String readAll(InputStream inputStream) throws IOException {
        StringBuilder out = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
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
            if (json.has("msg")) return json.getString("msg");
            if (json.has("error_description")) return json.getString("error_description");
            if (json.has("message")) return json.getString("message");
            if (json.has("error")) return json.getString("error");
        } catch (JSONException ignored) {
            return body == null || body.isBlank() ? "Authentication request failed." : body;
        }
        return "Authentication request failed.";
    }

    private String tryExtractField(String body, String field) {
        try {
            JSONObject json = new JSONObject(body);
            if (json.has(field)) return json.getString(field);
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