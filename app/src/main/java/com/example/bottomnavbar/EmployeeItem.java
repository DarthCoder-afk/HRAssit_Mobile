package com.example.bottomnavbar;

public class EmployeeItem {
    private String date;
    private int userProfileImage;
    private String username;
    private String position;
    private String content;
    private String status;


    public EmployeeItem(String date, int userProfileImage, String username, String position, String content, String status) {
        this.date = date;
        this.userProfileImage = userProfileImage;
        this.username = username;
        this.position = position;
        this.content = content;
        this.status = status;
    }


    public String getDate() {
        return date;
    }

    // Getter method for 'userProfileImage'
    public int getUserProfileImage() {
        return userProfileImage;
    }

    // Getter method for 'username'
    public String getUsername() {
        return username;
    }

    // Getter method for 'position'
    public String getPosition() {
        return position;
    }

    // Getter method for 'content'
    public String getContent() {
        return content;
    }

    // Getter method for 'status'
    public String getStatus() {
        return status;
    }
}
