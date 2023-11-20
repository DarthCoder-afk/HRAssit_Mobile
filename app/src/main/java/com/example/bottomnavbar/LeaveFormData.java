package com.example.bottomnavbar;

public class LeaveFormData {
    private String leaveType;
    private String startDate;
    private String endDate;
    private String headOfficer;
    private String reason;
    private String requestType;
    private String user_id;

    private String transaction_date;

    private String request_status;

    // Required no-argument constructor for Firestore
    public LeaveFormData() {
        // Default constructor required for calls to DataSnapshot.getValue(LeaveFormData.class)
    }

    public LeaveFormData(String leaveType, String startDate, String endDate, String headOfficer, String reason, String requestType, String user_id, String transaction_date, String request_status) {
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.headOfficer = headOfficer;
        this.reason = reason;
        this.requestType = requestType;
        this.user_id = user_id;
        this.transaction_date = transaction_date;
        this.request_status = request_status;
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

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
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
}
