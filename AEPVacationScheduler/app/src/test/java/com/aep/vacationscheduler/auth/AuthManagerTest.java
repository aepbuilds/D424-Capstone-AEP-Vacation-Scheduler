package com.aep.vacationscheduler.auth;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class AuthManagerTest {
    private Context context;
    private AuthManager authManager;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        // start each test from a clean slate, since SharedPreferences otherwise
        // persists between test methods within the same Robolectric run
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).edit().clear().commit();
        authManager = new AuthManager(context);
    }

    @Test
    public void hasAccount_beforeRegistration_returnsFalse() {
        assertFalse(authManager.hasAccount());
    }

    @Test
    public void registerAccount_thenHasAccount_returnsTrue() {
        authManager.registerAccount("morgan", "password123");
        assertTrue(authManager.hasAccount());
    }

    @Test
    public void login_correctCredentials_returnsTrueAndSetsLoggedIn() {
        authManager.registerAccount("morgan", "password123");
        assertTrue(authManager.login("morgan", "password123"));
        assertTrue(authManager.isLoggedIn());
    }

    @Test
    public void login_incorrectPassword_returnsFalse() {
        authManager.registerAccount("morgan", "password123");
        assertFalse(authManager.login("morgan", "wrongpassword"));
        assertFalse(authManager.isLoggedIn());
    }

    @Test
    public void password_isNotStoredInPlainText() {
        authManager.registerAccount("morgan", "password123");
        String storedHash = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                .getString("password_hash", null);
        assertNotNull(storedHash);
        assertNotEquals("password123", storedHash);
    }

    @Test
    public void fiveFailedAttempts_triggersLockout() {
        authManager.registerAccount("morgan", "password123");
        for (int i = 0; i < 5; i++) {
            authManager.login("morgan", "wrongpassword");
        }
        assertTrue(authManager.getRemainingLockoutMillis() > 0);
    }

    @Test
    public void login_duringLockout_rejectsEvenCorrectPassword() {
        authManager.registerAccount("morgan", "password123");
        for (int i = 0; i < 5; i++) {
            authManager.login("morgan", "wrongpassword");
        }
        // account is now locked out; even the correct password must be rejected
        assertFalse(authManager.login("morgan", "password123"));
    }

    @Test
    public void logout_setsIsLoggedInFalse() {
        authManager.registerAccount("morgan", "password123");
        authManager.login("morgan", "password123");
        authManager.logout();
        assertFalse(authManager.isLoggedIn());
    }
}