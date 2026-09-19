package com.aep.vacationscheduler.auth;

import android.content.Context;
import android.content.SharedPreferences;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AuthManager {
    private static final String PREFS_NAME = "auth_prefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD_HASH = "password_hash";
    private static final String KEY_SALT = "salt";
    private static final String KEY_FAILED_ATTEMPTS = "failed_attempts";
    private static final String KEY_LOCKOUT_UNTIL = "lockout_until";
    private static final String KEY_LOGGED_IN = "logged_in";

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = 30_000; // 30 seconds

    private final SharedPreferences prefs;

    public AuthManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean hasAccount() {
        return prefs.contains(KEY_USERNAME);
    }

    public void registerAccount(String username, String password) {
        String salt = generateSalt();
        String hash = hashPassword(password, salt);
        prefs.edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_PASSWORD_HASH, hash)
                .putString(KEY_SALT, salt)
                .putInt(KEY_FAILED_ATTEMPTS, 0)
                .putLong(KEY_LOCKOUT_UNTIL, 0)
                .apply();
    }

    public boolean login(String username, String password) {
        if (getRemainingLockoutMillis() > 0) {
            return false;
        }

        String storedUsername = prefs.getString(KEY_USERNAME, null);
        String storedSalt = prefs.getString(KEY_SALT, null);
        String storedHash = prefs.getString(KEY_PASSWORD_HASH, null);

        if (storedUsername == null || storedSalt == null || storedHash == null) {
            return false;
        }

        String attemptHash = hashPassword(password, storedSalt);
        boolean success = storedUsername.equals(username) && storedHash.equals(attemptHash);

        if (success) {
            prefs.edit()
                    .putInt(KEY_FAILED_ATTEMPTS, 0)
                    .putLong(KEY_LOCKOUT_UNTIL, 0)
                    .putBoolean(KEY_LOGGED_IN, true)
                    .apply();
        } else {
            recordFailedAttempt();
        }
        return success;
    }

    private void recordFailedAttempt() {
        int attempts = prefs.getInt(KEY_FAILED_ATTEMPTS, 0) + 1;
        SharedPreferences.Editor editor = prefs.edit().putInt(KEY_FAILED_ATTEMPTS, attempts);
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            editor.putLong(KEY_LOCKOUT_UNTIL, System.currentTimeMillis() + LOCKOUT_DURATION_MS);
            editor.putInt(KEY_FAILED_ATTEMPTS, 0);
        }
        editor.apply();
    }

    public long getRemainingLockoutMillis() {
        long lockoutUntil = prefs.getLong(KEY_LOCKOUT_UNTIL, 0);
        return Math.max(lockoutUntil - System.currentTimeMillis(), 0);
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    public void logout() {
        prefs.edit().putBoolean(KEY_LOGGED_IN, false).apply();
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : saltBytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt.getBytes());
            byte[] hashedBytes = digest.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
