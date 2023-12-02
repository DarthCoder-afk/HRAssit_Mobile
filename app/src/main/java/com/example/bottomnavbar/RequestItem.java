package com.example.bottomnavbar;

public class RequestItem {
    private String request_date;
    private int userProfileImage;
    private String request_username;
    private String position;

    private String requestType;

    private String documentID;


    public RequestItem(String date, int userProfileImage, String request_username, String position, String requestType,String documentID) {
        this.request_date = date;
        this.userProfileImage = userProfileImage;
        this.request_username = request_username;
        this.position = position;
        this.requestType = requestType;
        this.documentID = documentID;

    }

    public String getRequestType() {
        return requestType;
    }

    public String getRequest_date() {
        return request_date;
    }

    public int getUserProfileImage() {
        return userProfileImage;
    }

    public String getRequest_username() {
        return request_username;
    }

    public String getPosition() {
        return position;
    }

    public void setRequest_username(String request_username) {
        this.request_username = request_username;
    }
    public String getDocumentId() {
        return documentID;
    }
}
