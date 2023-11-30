package com.example.bottomnavbar;

public class LeaveRequestFormData {
    private String requestType;
    private String purpose;
    private String startDate;
    private String endDate;
    private String request_status;
    private String fileUrl;
    private String user_id;
    private String user_level;
    private String transaction_date;
    private String details_of_leave;
    private String specified;

    public LeaveRequestFormData(String requestType, String purpose, String startDate, String endDate,
                           String request_status, String fileUrl, String user_id, String user_level,
                           String transaction_date, String details_of_leave, String specified) {
        this.requestType = requestType;
        this.purpose = purpose;
        this.startDate = startDate;
        this.endDate = endDate;
        this.request_status = request_status;
        this.fileUrl = fileUrl;
        this.user_id = user_id;
        this.user_level = user_level;
        this.transaction_date = transaction_date;
        this.details_of_leave = details_of_leave;
        this.specified = specified;

    }

    public  LeaveRequestFormData(){

    }


    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
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

    public String getRequest_status() {
        return request_status;
    }

    public void setRequest_status(String request_status) {
        this.request_status = request_status;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
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

    public String getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }

    public String getDetails_of_leave() {
        return details_of_leave;
    }

    public void setDetails_of_leave(String details_of_leave) {
        this.details_of_leave = details_of_leave;
    }

    public String getSpecified() {
        return specified;
    }

    public void setSpecified(String specified) {
        this.specified = specified;
    }
}
