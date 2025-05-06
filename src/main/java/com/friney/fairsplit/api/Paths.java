package com.friney.fairsplit.api;

public final class Paths {
    public static final String AUTH = "/auth";
    public static final String USERS = "/users";
    public static final String EVENTS = "/events";
    public static final String EVENTS_ID = "/events/{eventId}";
    public static final String RECEIPTS = EVENTS_ID + "/receipts";
    public static final String RECEIPTS_ID = "/receipts/{receiptId}";
    public static final String EXPENSES = RECEIPTS_ID + "/expenses";
    public static final String EXPENSES_ID = "/expenses/{expenseId}";
    public static final String EXPENSES_MEMBERS = EXPENSES_ID + "/members";
    public static final String SUMMARY = EVENTS_ID + "/summary";

    private Paths() {
    }
}
