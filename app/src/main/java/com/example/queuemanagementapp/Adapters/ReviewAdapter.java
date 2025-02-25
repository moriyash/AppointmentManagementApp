package com.example.queuemanagementapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.queuemanagementapp.Classes.Review;
import com.example.queuemanagementapp.R;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private List<Review> reviewList;

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.txtReviewerName.setText(review.reviewerName);
        holder.txtReviewContent.setText(review.reviewContent);
        holder.txtReviewDate.setText(review.date);
        holder.ratingBar.setRating(review.rating);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtReviewerName, txtReviewContent, txtReviewDate;
        RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            txtReviewerName = itemView.findViewById(R.id.txt_reviewer_name);
            txtReviewContent = itemView.findViewById(R.id.txt_review_content);
            txtReviewDate = itemView.findViewById(R.id.txt_review_date);
            ratingBar = itemView.findViewById(R.id.rating_bar);
        }
    }
}
