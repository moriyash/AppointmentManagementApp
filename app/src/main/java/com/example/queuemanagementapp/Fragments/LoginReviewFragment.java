package com.example.queuemanagementapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.queuemanagementapp.Activities.MainActivity;
import com.example.queuemanagementapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

public class LoginReviewFragment extends Fragment {

    private EditText edtPhoneNumber;
    private Button btnViewReviews, btnBackMain;

    public LoginReviewFragment() {
        // קונסטרקטור ריק חובה
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_review, container, false);

        // אתחול רכיבים
        edtPhoneNumber = view.findViewById(R.id.edt_phone_number);
        btnViewReviews = view.findViewById(R.id.btn_view_reviews);
        btnBackMain = view.findViewById(R.id.btn_back_main);

        btnViewReviews.setOnClickListener(v -> {
            String phoneNumber = edtPhoneNumber.getText().toString().trim();
            if (phoneNumber.isEmpty()) {
                Toast.makeText(getContext(), "אנא הזן מספר טלפון", Toast.LENGTH_SHORT).show();
                return;
            }

            // הסרת קידומת כדי לוודא שהמספר נשמר בפורמט תקין (05XXXXXXXX)
            phoneNumber = cleanPhoneNumber(phoneNumber);
            // הגדרת משתנה final לשימוש בתוך ה-inner class
            final String finalPhoneNumber = phoneNumber;

            // בדיקה במערכת אם המספר קיים
            DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Users");
            usersReference.child(finalPhoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // המספר קיים – נווט לעמוד הביקורות והעבר את המספר ב־Bundle
                        Bundle bundle = new Bundle();
                        bundle.putString("phoneNumber", finalPhoneNumber);
                        Navigation.findNavController(v)
                                .navigate(R.id.action_loginReviewFragment_to_reviewsFragment, bundle);
                    } else {
                        Toast.makeText(getContext(), "מספר טלפון לא נמצא במערכת", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "שגיאה בבדיקת מספר טלפון", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // כפתור חזרה לעמוד הראשי
        btnBackMain.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }


    private String cleanPhoneNumber(String phone) {
        if (phone.startsWith("+972")) {
            return "0" + phone.substring(4);
        } else if (phone.startsWith("972")) {
            return "0" + phone.substring(3);
        }
        return phone;
    }
}
