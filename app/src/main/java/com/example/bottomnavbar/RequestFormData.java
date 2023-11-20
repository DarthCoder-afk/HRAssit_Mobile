package com.example.bottomnavbar;

public class RequestFormData {
    private String purpose;
    private String startDate;
    private String endDate;
    private String headOfficer;
    private String reason;
    private String requestType;
    private String user_id;

    private String transaction_date;

    private String request_status;

    private String first_name;

    private String last_name;

    private String user_level;

    // Required no-argument constructor for Firestore
    public RequestFormData() {
        // Default constructor required for calls to DataSnapshot.getValue(LeaveFormData.class)
    }



    public RequestFormData(String purpose, String startDate, String endDate, String headOfficer, String reason, String requestType, String user_id, String transaction_date,
                           String request_status, String first_name, String last_name, String user_level) {
        this.purpose = purpose;
        this.startDate = startDate;
        this.endDate = endDate;
        this.headOfficer = headOfficer;
        this.reason = reason;
        this.requestType = requestType;
        this.user_id = user_id;
        this.transaction_date = transaction_date;
        this.request_status = request_status;
        this.first_name = first_name;
        this.last_name = last_name;
        this.user_level = user_level;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getTransaction_date() {
        return transaction_date;
    }

    public String getRequest_status() {
        return request_status;
    }
// Getters and setters


    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }

    public void setRequest_status(String request_status) {
        this.request_status = request_status;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getHeadOfficer() {
        return headOfficer;
    }

    public void setHeadOfficer(String headOfficer) {
        this.headOfficer = headOfficer;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_level() {
        return user_level;
    }

    public void setUser_level(String user_level) {
        this.user_level = user_level;
    }
}
