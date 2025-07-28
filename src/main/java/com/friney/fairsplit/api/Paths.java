package com.friney.fairsplit.api;

public final class Paths {
    public static final String API = "/api";
    public static final String V1 = "/v1";
    public static final String AUTH_V1 = API + V1 + "/auth";
    public static final String USERS_V1 = API + V1 + "/users";
    public static final String EVENTS_V1 = API + V1 + "/events";
    public static final String EVENTS_ID_V1 = API + V1 + "/events/{eventId}";
    public static final String RECEIPTS_V1 = EVENTS_ID_V1 + "/receipts";
    public static final String RECEIPTS_ID_V1 = API + V1 + "/receipts/{receiptId}";
    public static final String EXPENSES_V1 = RECEIPTS_ID_V1 + "/expenses";
    public static final String EXPENSES_ID_V1 = API + V1 + "/expenses/{expenseId}";
    public static final String EXPENSES_MEMBERS_V1 = EXPENSES_ID_V1 + "/members";
    public static final String SUMMARY_V1 = EVENTS_ID_V1 + "/summary";

    private Paths() {
    }
}
