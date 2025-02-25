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

    public SelectDateFragment() {
        // קונסטרקטור ריק חובה
    }

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

            if (selectedTime.contains("⛔")) {
                Toast.makeText(getContext(), "השעה שבחרת כבר תפוסה", Toast.LENGTH_SHORT).show();
                return;
            }

            checkIfTimeIsAvailable(selectedDate, selectedTime);
        });

        btnBack.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());

        return view;
    }

    /**
     * בדיקה האם תאריך מסוים הוא שבת
     */
    private boolean isSaturday(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
    }

    /**
     * בדיקה האם תאריך מסוים הוא יום שישי
     */
    private boolean isFriday(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY;
    }

    /**
     * פתיחת לוח השנה ובדיקת תאריך קיים
     */
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            if (isSaturday(selectedYear, selectedMonth, selectedDay)) {
                Toast.makeText(getContext(), "🚫 אין קביעת תורים בשבת. אנא בחר יום אחר.", Toast.LENGTH_SHORT).show();
            } else {
                selectedDate = formatDate(selectedDay, selectedMonth + 1, selectedYear);
                checkIfDateIsBlocked(selectedDate, selectedYear, selectedMonth, selectedDay);
            }
        }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()); // מונע בחירה של תאריכים קודמים
        datePickerDialog.show();
    }
    /**
     * בדיקת האם השעה שבחרו זמינה **לפני השמירה**
     */
    private void checkIfTimeIsAvailable(String date, String time) {
        appointmentsReference.child(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isTimeTaken = false;

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    if (userSnapshot.hasChild(time)) {
                        isTimeTaken = true;
                        break;
                    }
                }

                if (isTimeTaken) {
                    Toast.makeText(getContext(), "השעה שבחרת כבר תפוסה!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "שגיאה בבדיקת זמינות השעה.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * בדיקת האם תאריך זה חסום על ידי המנהל
     */
    private void checkIfDateIsBlocked(String date, int year, int month, int day) {
        workdaysReference.child(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String dayType = snapshot.child("type").getValue(String.class);
                    String hours = snapshot.child("hours").getValue(String.class);

                    if (dayType.equals("יום חופשה") || dayType.equals("יום מחלה")) {
                        selectedDateText.setText("❌ התאריך חסום: " + date);
                        isDateBlocked = true;
                        timeSpinner.setEnabled(false);
                        btnNext.setEnabled(false);
                    } else {
                        selectedDateText.setText("✅ תאריך נבחר: " + date);
                        isDateBlocked = false;
                        timeSpinner.setEnabled(true);
                        btnNext.setEnabled(true);
                        loadAvailableTimes(date, year, month, day);
                    }
                } else {
                    selectedDateText.setText("✅ תאריך נבחר: " + date);
                    isDateBlocked = false;
                    timeSpinner.setEnabled(true);
                    btnNext.setEnabled(true);
                    loadAvailableTimes(date, year, month, day);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "שגיאה בבדיקת זמינות תאריך.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * טעינת השעות הפנויות בהתאם ליום (יום שישי שעות מיוחדות)
     */
    private void loadAvailableTimes(String date, int year, int month, int day) {
        availableTimes = new ArrayList<>();
        bookedTimes = new ArrayList<>();

        if (isFriday(year, month, day)) {
            availableTimes = Arrays.asList("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00");
        } else {
            availableTimes = Arrays.asList("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00");
        }

        updateTimeSpinner();
    }

    /**
     * עדכון ה-Spinner עם השעות הפנויות והשעות התפוסות
     */
    private void updateTimeSpinner() {
        List<String> displayTimes = new ArrayList<>();

        for (String time : availableTimes) {
            if (bookedTimes.contains(time)) {
                displayTimes.add(time + " ⛔ תפוס"); // מסמן שעות תפוסות
            } else {
                displayTimes.add(time);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, displayTimes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(adapter);
    }

    /**
     * המרת תאריך לפורמט `DD-MM-YYYY`
     */
    private String formatDate(int day, int month, int year) {
        return String.format("%02d-%02d-%04d", day, month, year);
    }
}
