package com.max_griffin_project;

public class AppConstants {
    // Access token expires in 30 minutes (in milliseconds)
    public static final long JWT_EXPIRES_IN = 30000; //1_800_000;

    // Refresh token expires in 7 days (in milliseconds)
    public static final long JWT_REFRESH_EXPIRES_IN = 7 * 24 * 60 * 60 * 1000L; // 604800000 ms

    // Load the secret from an environment variable, with a fallback for development.
    public static final String JWT_SECRET = System.getenv("JWT_SECRET") != null
            ? System.getenv("JWT_SECRET")
            : "plz dont steal this"; // TODO: In production, ensure this secret is securely stored.
}
