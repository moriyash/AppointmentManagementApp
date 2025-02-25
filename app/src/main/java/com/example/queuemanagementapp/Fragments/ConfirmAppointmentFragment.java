package com.example.queuemanagementapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.queuemanagementapp.Activities.MainActivity;
import com.example.queuemanagementapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ConfirmAppointmentFragment extends Fragment {
    private FirebaseFirestore db;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String selectedDate;
    private String selectedTime;
    private String selectedService;
    private String phoneNumber;

    public ConfirmAppointmentFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm_appointment, container, false);

        // אתחול Firebase
        db = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users"); // חיבור ל-"Users"

        // קבלת פרטי התור שנבחרו
        selectedDate = getArguments().getString("selectedDate");
        selectedTime = getArguments().getString("selectedTime");
        selectedService = getArguments().getString("selectedService", "לא נבחר שירות"); // ✅ ערך ברירת מחדל

        TextView txtConfirmDetails = view.findViewById(R.id.txt_confirm_details);
        txtConfirmDetails.setText("תאריך: " + selectedDate + "\nשעה: " + selectedTime + "\nשירות: " + selectedService);

        Button btnConfirm = view.findViewById(R.id.btn_confirm_appointment);
        btnConfirm.setOnClickListener(v -> getLastPhoneNumberAndSave(view));

        return view;
    }

    private void getLastPhoneNumberAndSave(View view) {
        // Retrieve phone number
        database.getReference("Users").limitToLast(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().hasChildren()) {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    phoneNumber = snapshot.getValue(String.class);

                    // ✅ בדיקה אם המספר קיים
                    if (phoneNumber == null || phoneNumber.isEmpty()) {
                        Toast.makeText(getContext(), "שגיאה: מספר טלפון לא נמצא", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    saveAppointment(phoneNumber, view);
                    return;
                }
            } else {
                Toast.makeText(getContext(), "שגיאה: מספר טלפון לא נמצא", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAppointment(String phoneNumber, View view) {
        String appointmentId = selectedDate + "_" + selectedTime; // מזהה ייחודי לכל תור

        // יצירת נתוני התור
        Map<String, Object> appointment = new HashMap<>();
        appointment.put("phone", phoneNumber);
        appointment.put("date", selectedDate);
        appointment.put("time", selectedTime);
        appointment.put("service", selectedService);

        // שמירה ב-Firebase
        DatabaseReference appointmentRef = database.getReference("appointments").child(phoneNumber).child(appointmentId);
        appointmentRef.setValue(appointment)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "התור נשמר בהצלחה!", Toast.LENGTH_SHORT).show();

                    // יצירת Intent לעמוד הראשי (MainActivity)
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  // לוודא שכל ה-Activities הקודמים ייסגרו
                    startActivity(intent); // הפעלת ה-Activity
                    requireActivity().finish(); // מסיים את ה-Activity הנוכחי
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "שגיאה בשמירת התור", Toast.LENGTH_SHORT).show());
    }
}
