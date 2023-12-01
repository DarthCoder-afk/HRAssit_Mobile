package com.example.bottomnavbar;

public class HistoryItem {

    private String date;
    private String content;

    private String documentId;  // Add this field

    public HistoryItem(String date, String content, String documentId) {
        this.date = date;
        this.content = content;
        this.documentId = documentId;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public String getDocumentId() {
        return documentId;
    }
}
