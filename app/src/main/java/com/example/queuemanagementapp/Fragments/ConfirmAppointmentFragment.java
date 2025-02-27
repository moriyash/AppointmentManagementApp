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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
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
                    // phoneNumber = snapshot.getValue(String.class);
                    phoneNumber = EnterPhoneFragment.getLastPhoneNumber();
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
        DatabaseReference userAppointmentsRef = database.getReference("appointments").child(phoneNumber);
        System.out.println(phoneNumber);

        userAppointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int nextAppointmentNumber = (int) snapshot.getChildrenCount() + 1; // מספר התור הבא

                String appointmentKey = String.valueOf(nextAppointmentNumber); // קביעת מספר סידורי לתור

                DatabaseReference appointmentRef = userAppointmentsRef.child(appointmentKey);

                Map<String, Object> appointment = new HashMap<>();
                appointment.put("phone", phoneNumber);
                appointment.put("date", selectedDate);
                appointment.put("time", selectedTime);
                appointment.put("service", selectedService);

                appointmentRef.setValue(appointment)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "✅ התור נשמר בהצלחה!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            requireActivity().finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "❌ שגיאה בשמירת התור", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "⚠ שגיאה בגישה ל-Firebase.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
