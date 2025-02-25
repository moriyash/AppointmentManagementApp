package com.example.queuemanagementapp.Classes;

public class Review {
    public String phoneNumber;     // מספר הטלפון של המשתמש
    public String reviewerName;    // שם המשתמש או "משתמש אנונימי"
    public String reviewContent;   // תוכן הביקורת
    public String date;            // תאריך הביקורת בפורמט dd/MM/yyyy
    public float rating;           // דירוג (float)

    // קונסטרקטור ריק נדרש ל-Firebase
    public Review() {}

    public Review(String phoneNumber, String reviewerName, float rating, String reviewContent, String date) {
        this.phoneNumber = phoneNumber;
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.reviewContent = reviewContent;
        this.date = date;
    }
}
