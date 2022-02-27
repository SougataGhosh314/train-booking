package com.sougata.train_booking.config;

public class CoachConfig {
    public static final String maxCapACSleeper;
    public static final String maxCapNonACSleeper;
    public static final String maxCapSeater;

    static {
        maxCapACSleeper = "60";
        maxCapNonACSleeper = "60";
        maxCapSeater = "120";
    }
}
