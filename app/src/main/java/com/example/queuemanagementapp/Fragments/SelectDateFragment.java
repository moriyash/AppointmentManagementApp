package com.example.queuemanagementapp.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.queuemanagementapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class SelectDateFragment extends Fragment {
    private TextView selectedDateText;
    private Spinner timeSpinner;
    private Button btnPickDate, btnNext, btnBack;
    private String selectedDate = "", selectedTime = "";
    private FirebaseDatabase database;
    private DatabaseReference workdaysReference, appointmentsReference;
    private boolean isDateBlocked = false;
    private List<String> availableTimes, bookedTimes;

    public SelectDateFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_date, container, false);

        selectedDateText = view.findViewById(R.id.selected_date_text);
        timeSpinner = view.findViewById(R.id.time_spinner);
        btnPickDate = view.findViewById(R.id.btn_pick_date);
        btnNext = view.findViewById(R.id.btn_next);
        btnBack = view.findViewById(R.id.btn_back);

        database = FirebaseDatabase.getInstance();
        workdaysReference = database.getReference("workdays");
        appointmentsReference = database.getReference("appointments");

        btnPickDate.setOnClickListener(v -> showDatePicker());

        btnNext.setOnClickListener(v -> {
            if (selectedDate.isEmpty()) {
                Toast.makeText(getContext(), "אנא בחר תאריך", Toast.LENGTH_SHORT).show();
                return;
            }
            selectedTime = timeSpinner.getSelectedItem().toString();

            if (selectedTime.contains("blocked")) {
                Toast.makeText(getContext(), "השעה שבחרת כבר תפוסה", Toast.LENGTH_SHORT).show();
                return;
            }

            checkIfTimeIsAvailable(selectedDate, selectedTime);
        });

        btnBack.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());

        return view;
    }

    private boolean isSaturday(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
    }





    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            if (isSaturday(selectedYear, selectedMonth, selectedDay)) {
                Toast.makeText(getContext(), " אין קביעת תורים בשבת. אנא בחר יום אחר.", Toast.LENGTH_SHORT).show();
            } else {
                selectedDate = formatDate(selectedDay, selectedMonth + 1, selectedYear);

                workdaysReference.child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String workingHours;
                        String dayType = snapshot.child("type").getValue(String.class);

                        if (dayType != null && (dayType.equals("יום חופשה") || dayType.equals("יום מחלה"))) {
                            Toast.makeText(getContext(), " יום זה חסום על ידי המנהל. בחר תאריך אחר.", Toast.LENGTH_SHORT).show();
                            selectedDate = "";
                            return;
                        }

                        if (snapshot.exists() && snapshot.child("hours").exists()) {
                            workingHours = snapshot.child("hours").getValue(String.class);
                        } else {
                            workingHours = getDefaultWorkingHours(selectedYear, selectedMonth, selectedDay);
                        }

                        loadAvailableTimes(selectedDate, workingHours);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), " שגיאה בטעינת שעות העבודה.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    private String getDefaultWorkingHours(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            return "08:00 - 14:00";
        } else {
            return "08:00 - 19:00";
        }
    }



    private void loadAvailableTimes(String date, String workingHours) {
        availableTimes = new ArrayList<>();
        bookedTimes = new ArrayList<>();

        String[] hoursSplit = workingHours.split(" - ");
        if (hoursSplit.length == 2) {
            String startHour = hoursSplit[0];
            String endHour = hoursSplit[1];
            availableTimes = generateTimeSlots(startHour, endHour);
        } else {
            availableTimes = Arrays.asList("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00");
        }

        updateTimeSpinner();
    }
    private void updateClientWorkingHours(String date, String workingHours) {
        DatabaseReference clientsRef = FirebaseDatabase.getInstance().getReference("appointments");

        clientsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot appointmentSnapshot : userSnapshot.getChildren()) {
                        String appointmentDate = appointmentSnapshot.child("date").getValue(String.class);

                        if (appointmentDate != null && appointmentDate.equals(date)) {
                            // עדכון שעות העבודה בתורים של הלקוחות
                            appointmentSnapshot.getRef().child("working_hours").setValue(workingHours);
                        }
                    }
                }
                Toast.makeText(getContext(), " שעות העבודה עודכנו אצל הלקוחות!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), " שגיאה בעדכון שעות העבודה ללקוחות.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private List<String> generateTimeSlots(String startHour, String endHour) {
        List<String> slots = new ArrayList<>();
        int start = Integer.parseInt(startHour.split(":")[0]);
        int end = Integer.parseInt(endHour.split(":")[0]);

        for (int i = start; i < end; i++) {
            slots.add(String.format("%02d:00", i));
        }
        return slots;
    }

    private void updateTimeSpinner() {
        List<String> displayTimes = new ArrayList<>();

        for (String time : availableTimes) {
            if (bookedTimes.contains(time)) {
                displayTimes.add(time + "  תפוס");
            } else {
                displayTimes.add(time);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, displayTimes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(adapter);
    }

    private void checkIfTimeIsAvailable(String date, String time) {
        appointmentsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isTimeTaken = false;

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot appointmentSnapshot : userSnapshot.getChildren()) {
                        String bookedDate = appointmentSnapshot.child("date").getValue(String.class);
                        String bookedTime = appointmentSnapshot.child("time").getValue(String.class);

                        if (bookedDate != null && bookedTime != null && bookedDate.equals(date) && bookedTime.equals(time)) {
                            isTimeTaken = true;
                            break;
                        }
                    }
                    if (isTimeTaken) break;
                }

                if (isTimeTaken) {
                    Toast.makeText(getContext(), " השעה שבחרת כבר תפוסה!", Toast.LENGTH_SHORT).show();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("selectedDate", selectedDate);
                    bundle.putString("selectedTime", selectedTime);
                    bundle.putString("selectedService", getArguments().getString("selectedService", "לא נבחר שירות"));

                    Navigation.findNavController(getView()).navigate(R.id.action_selectDateFragment_to_confirmAppointmentFragment, bundle);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), " שגיאה בבדיקת זמינות השעה.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatDate(int day, int month, int year) {
        return String.format("%02d-%02d-%04d", day, month, year);
    }
}