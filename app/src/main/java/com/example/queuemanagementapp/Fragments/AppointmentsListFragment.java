package com.example.queuemanagementapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.queuemanagementapp.Activities.MainActivity;
import com.example.queuemanagementapp.Adapters.AppointmentsAdapter;
import com.example.queuemanagementapp.R;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class AppointmentsListFragment extends Fragment {

    private RecyclerView recyclerAppointments;
    private AppointmentsAdapter appointmentsAdapter;
    private DatabaseReference appointmentsReference;
    private String phoneNumber;
    private Button btnBackHome;

    public AppointmentsListFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointments_list, container, false);

        recyclerAppointments = view.findViewById(R.id.recycler_appointments);
        btnBackHome = view.findViewById(R.id.btn_back_home);

        recyclerAppointments.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            phoneNumber = getArguments().getString("phoneNumber");
        }

        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Toast.makeText(getContext(), "שגיאה: מספר טלפון חסר", Toast.LENGTH_SHORT).show();
            return view;
        }

        appointmentsReference = FirebaseDatabase.getInstance().getReference("appointments");
        loadAppointmentsFromFirebase(phoneNumber);

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }

    private void loadAppointmentsFromFirebase(String phone) {
        Log.d("FirebaseData", "מחפש תורים למספר: " + phone);

        appointmentsReference.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> appointmentsList = new ArrayList<>();

                if (!snapshot.exists()) {
                    Log.d("FirebaseData", "לא נמצאו תורים במסד הנתונים.");
                    Toast.makeText(getContext(), "אין תורים זמינים למספר זה", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot appointmentSnapshot : snapshot.getChildren()) {
                    String date = appointmentSnapshot.child("date").getValue(String.class);
                    String time = appointmentSnapshot.child("time").getValue(String.class);
                    String status = appointmentSnapshot.child("status").getValue(String.class);

                    if (date != null && time != null) {
                        String appointmentText = "📅 תאריך: " + date + " | ⏰ שעה: " + time;

                        // הוספת אינדיקציה אם התור בוטל
                        if (status != null && status.equals("בוטל עקב חופשה/מחלה")) {
                            appointmentText += " (🚫 התור בוטל עקב חופשה/מחלה)";
                        }

                        appointmentsList.add(appointmentText);
                    }
                }

                if (appointmentsList.isEmpty()) {
                    Log.d("FirebaseData", "רשימת התורים ריקה.");
                    Toast.makeText(getContext(), "לא נמצאו תורים למספר זה", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("FirebaseData", "סה\"כ תורים שנמצאו: " + appointmentsList.size());

                    appointmentsAdapter = new AppointmentsAdapter(appointmentsList, phoneNumber, appointmentsReference);
                    recyclerAppointments.setAdapter(appointmentsAdapter);
                    recyclerAppointments.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "שגיאה בבדיקת התורים: " + error.getMessage());
                Toast.makeText(getContext(), "שגיאה בטעינת התורים", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
