package com.example.queuemanagementapp.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.queuemanagementapp.Adapters.ReviewAdapter;
import com.example.queuemanagementapp.Classes.Review;
import com.example.queuemanagementapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewFragment extends Fragment {

    private EditText edtReviewerName, edtReviewContent;
    private Button btnSubmitReview, btnBackMain;
    private RatingBar ratingBar;
    private ProgressBar progressBar;
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private DatabaseReference reviewsReference;
    private String userPhoneNumber;
    private List<Review> reviewList = new ArrayList<>();

    public ReviewFragment() {
        // קונסטרקטור ריק חובה
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // קבלת מספר הטלפון מה־Bundle
        if (getArguments() != null) {
            userPhoneNumber = getArguments().getString("phoneNumber", "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);

        // אתחול רכיבים – כאן אין צורך בשדה להזנת מספר טלפון
        edtReviewerName = view.findViewById(R.id.edt_reviewer_name);
        edtReviewContent = view.findViewById(R.id.edt_review_content);
        ratingBar = view.findViewById(R.id.rating_bar);
        btnSubmitReview = view.findViewById(R.id.btn_submit_review);
        btnBackMain = view.findViewById(R.id.btn_back_main); // אתחול כפתור חזור
        progressBar = view.findViewById(R.id.progressBar);
        recyclerViewReviews = view.findViewById(R.id.recycler_reviews);

        reviewsReference = FirebaseDatabase.getInstance().getReference("Reviews");

        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        reviewAdapter = new ReviewAdapter(reviewList);
        recyclerViewReviews.setAdapter(reviewAdapter);

        btnSubmitReview.setOnClickListener(v -> submitReview());
        // הוספת מאזין לכפתור חזור
        btnBackMain.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        loadReviews();

        return view;
    }

    private void loadReviews() {
        reviewsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewList.clear();
                for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                    Review review = reviewSnapshot.getValue(Review.class);
                    reviewList.add(review);
                }
                reviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "שגיאה בטעינת ביקורות", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitReview() {
        String content = edtReviewContent.getText().toString().trim();
        String name = edtReviewerName.getText().toString().trim();
        float rating = ratingBar.getRating();

        if (TextUtils.isEmpty(content) || rating == 0) {
            Toast.makeText(getContext(), "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        String reviewId = reviewsReference.push().getKey();
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        Review review = new Review(userPhoneNumber, name.isEmpty() ? "משתמש אנונימי" : name, rating, content, date);

        reviewsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long nextId = snapshot.getChildrenCount() + 1; // מספר סידורי לביקורת החדשה
                String reviewId = String.valueOf(nextId);

                String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                Review review = new Review(userPhoneNumber, name.isEmpty() ? "משתמש אנונימי" : name, rating, content, date);

                reviewsReference.child(reviewId).setValue(review).addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "ביקורת נשמרה!", Toast.LENGTH_SHORT).show();
                        edtReviewContent.setText("");
                        edtReviewerName.setText("");
                        ratingBar.setRating(0);
                    } else {
                        Toast.makeText(getContext(), "שגיאה בשליחת הביקורת", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "שגיאה בקבלת מספר ביקורת", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
