package com.example.bottomnavbar;

public class HistoryItem {

    private String date;
    private String content;

    public HistoryItem(String date, String content) {
        this.date = date;
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }
}
