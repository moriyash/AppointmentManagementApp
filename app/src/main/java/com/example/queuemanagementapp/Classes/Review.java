package com.example.queuemanagementapp.Classes;

public class Review {
    public String phoneNumber;     //phone user
    public String reviewerName;    // username
    public String reviewContent;   // content of review
    public String date;            // תאריך הביקורת בפורמט dd/MM/yyyy
    public float rating;           // rate


    public Review() {}

    public Review(String phoneNumber, String reviewerName, float rating, String reviewContent, String date) {
        this.phoneNumber = phoneNumber;
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.reviewContent = reviewContent;
        this.date = date;
    }
}
