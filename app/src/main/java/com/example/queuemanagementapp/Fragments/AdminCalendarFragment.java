package com.example.queuemanagementapp.Fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdminCalendarFragment extends Fragment {
    private TextView selectedDateText;
    private Button btnPickDate, btnConfirmDayType, btnBack;
    private String selectedDate = "", selectedDayType = "", selectedHours = "";
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    public AdminCalendarFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_calendar, container, false);

        selectedDateText = view.findViewById(R.id.selected_date_text);
        btnPickDate = view.findViewById(R.id.btn_pick_date);
        btnConfirmDayType = view.findViewById(R.id.btn_confirm_day_type);
        btnBack = view.findViewById(R.id.btn_admin_back);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("workdays");

        btnPickDate.setOnClickListener(v -> showDatePicker());

        btnConfirmDayType.setOnClickListener(v -> {
            if (!selectedDate.isEmpty() && !selectedDayType.isEmpty()) {
                saveDayTypeToFirebase(selectedDate, selectedDayType, selectedHours);
                Toast.makeText(getContext(), "✅ השינוי נשמר בהצלחה!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "⚠ אנא בחר תאריך וסוג יום לפני האישור.", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish(); // סוגר את הפרגמנט ומחזיר לעמוד הראשי
        });

        return view; // ✅ הוספתי את החזרת ה-View החסרה
    }

    private boolean isSaturday(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year, month, day) -> {

            // בדיקה אם היום שנבחר הוא שבת
            if (isSaturday(year, month, day)) {
                Toast.makeText(getContext(), "🚫 אין אפשרות לקבוע ימי שבת.", Toast.LENGTH_SHORT).show();
                return;
            }

            selectedDate = String.format("%02d-%02d-%04d", day, month + 1, year);
            checkExistingDayType(selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }


    private void checkExistingDayType(String date) {
        databaseReference.child(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    selectedDayType = snapshot.child("type").getValue(String.class);
                    selectedHours = snapshot.child("hours").getValue(String.class);
                    selectedDateText.setText("📅 תאריך: " + date + "\n📝 סוג יום: " + selectedDayType + "\n⏰ שעות: " + selectedHours);

                    // ✅ פותח אוטומטית את תיבת האפשרויות אם כבר הוגדר סוג יום
                    showDayTypeDialog();
                } else {
                    showDayTypeDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                selectedDateText.setText("❌ שגיאה בטעינת הנתונים.");
            }
        });
    }

    private void showTimePicker(String date, String dayType) {
        Calendar calendar = Calendar.getInstance();

        // בחירת שעת התחלה
        TimePickerDialog startPicker = new TimePickerDialog(getContext(), (view, startHour, startMinute) -> {
            String startTime = String.format("%02d:%02d", startHour, startMinute);

            // בחירת שעת סיום אחרי שעת ההתחלה
            TimePickerDialog endPicker = new TimePickerDialog(getContext(), (view2, endHour, endMinute) -> {
                String endTime = String.format("%02d:%02d", endHour, endMinute);

                // ✅ שמירת השעות החדשות במסד הנתונים
                String workingHours = startTime + " - " + endTime;
                databaseReference.child(date).child("type").setValue(dayType);
                databaseReference.child(date).child("hours").setValue(workingHours);

                Toast.makeText(getContext(), "✅ שעות העבודה נשמרו: " + workingHours, Toast.LENGTH_SHORT).show();

                // ✅ קריאה לפונקציה שמעדכנת את שעות העבודה של הלקוחות!
                updateClientWorkingHours(date, workingHours);

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

            endPicker.setTitle("בחר שעת סיום");
            endPicker.show();

        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

        startPicker.setTitle("בחר שעת התחלה");
        startPicker.show();
    }


    private List<String> generateValidTimeSlots(String workingHours) {
        List<String> slots = new ArrayList<>();
        String[] hoursSplit = workingHours.split(" - ");

        if (hoursSplit.length == 2) {
            int start = Integer.parseInt(hoursSplit[0].split(":")[0]);
            int end = Integer.parseInt(hoursSplit[1].split(":")[0]);

            for (int i = start; i < end; i++) {
                slots.add(String.format("%02d:00", i));
            }
        }
        return slots;
    }

    private void showDayTypeDialog() {
        String[] dayTypes = {"יום חופשה", "יום מחלה", "שעות עבודה", "בטל שינוי"};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("📆 בחר סוג יום")
                .setItems(dayTypes, (dialog, which) -> {
                    selectedDayType = dayTypes[which];

                    if (selectedDayType.equals("יום חופשה") || selectedDayType.equals("יום מחלה")) {
                        saveDayTypeToFirebase(selectedDate, selectedDayType, "");
                    } else if (selectedDayType.equals("שעות עבודה")) {
                        //  כאן קוראים לפונקציה שתפתח את תיבת הבחירה לשעות העבודה
                        showTimePicker(selectedDate, selectedDayType);
                    } else if (selectedDayType.equals("בטל שינוי")) {
                        removeDayTypeFromFirebase(selectedDate);
                    }
                });
        builder.create().show();
    }
    private void updateClientWorkingHours(String date, String workingHours) {
        DatabaseReference clientsRef = FirebaseDatabase.getInstance().getReference("appointments");

        clientsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean hasChanges = false; // לבדוק אם בוצעו שינויים

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot appointmentSnapshot : userSnapshot.getChildren()) {
                        String appointmentDate = appointmentSnapshot.child("date").getValue(String.class);
                        String appointmentTime = appointmentSnapshot.child("time").getValue(String.class);

                        if (appointmentDate != null && appointmentDate.equals(date)) {
                            List<String> validTimes = generateValidTimeSlots(workingHours);

                            if (!validTimes.contains(appointmentTime)) {
                                // ביטול תורים שלא תואמים לשעות החדשות
                                appointmentSnapshot.getRef().child("status").setValue("🚫 בוטל עקב שינוי שעות עבודה");
                                hasChanges = true;
                            }
                        }
                    }
                }

                // שמירת השעות החדשות גם ב-`workdays`
                databaseReference.child(date).child("hours").setValue(workingHours);

                if (hasChanges) {
                    Toast.makeText(getContext(), "📅 התורים שלא התאימו לשעות החדשות בוטלו.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "⚠ שגיאה בעדכון שעות העבודה ללקוחות.", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void saveDayTypeToFirebase(String date, String dayType, String hours) {
        databaseReference.child(date).child("type").setValue(dayType);
        databaseReference.child(date).child("hours").setValue(hours);

        if (dayType.equals("יום חופשה") || dayType.equals("יום מחלה")) {
            cancelAppointmentsForDate(date);
        }
        Toast.makeText(getContext(), "✅ השינוי נשמר: " + dayType, Toast.LENGTH_SHORT).show();
    }

    private void removeDayTypeFromFirebase(String date) {
        databaseReference.child(date).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                restoreAppointmentsForDate(date);
                Toast.makeText(getContext(), "🔄 השינוי בוטל, התורים חזרו למצבם הקודם.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "❌ שגיאה בביטול השינוי.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void restoreAppointmentsForDate(String date) {
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");

        appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot appointmentSnapshot : userSnapshot.getChildren()) {
                        String appointmentDate = appointmentSnapshot.child("date").getValue(String.class);

                        if (appointmentDate != null && appointmentDate.equals(date)) {
                            appointmentSnapshot.getRef().child("status").removeValue(); // מחיקת הסטטוס

                            // מחיקת ההתראה למשתמש
                            String userPhone = userSnapshot.getKey();
                            removeNotificationForUser(userPhone);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "⚠ שגיאה בשחזור התורים.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeNotificationForUser(String phoneNumber) {
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("notifications").child(phoneNumber);
        notificationsRef.removeValue();
    }

    private void cancelAppointmentsForDate(String date) {
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");

        appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userPhone = userSnapshot.getKey(); // שליפת מספר הטלפון של המשתמש

                    boolean hasCanceledAppointments = false; // נבדוק אם יש ביטולים

                    for (DataSnapshot appointmentSnapshot : userSnapshot.getChildren()) {
                        String appointmentDate = appointmentSnapshot.child("date").getValue(String.class);

                        if (appointmentDate != null && appointmentDate.equals(date)) {
                            appointmentSnapshot.getRef().child("status").setValue("🚫 בוטל עקב חופשה/מחלה");
                            hasCanceledAppointments = true;
                        }
                    }

                    // אם המשתמש איבד תור, שלח לו התראה
                    if (hasCanceledAppointments) {
                        sendNotificationToUser(userPhone);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "⚠ שגיאה בעדכון תורים מבוטלים.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * שליחת הודעה למשתמש שהתור שלו בוטל
     */
    private void sendNotificationToUser(String phoneNumber) {
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("notifications").child(phoneNumber);
        String notificationId = notificationsRef.push().getKey();
        String notificationKey = "status";
        if (notificationId != null) {
            notificationsRef.child(notificationKey).setValue("🚫 התור שלך בוטל עקב חופשה/מחלה");        }

        // 🔽 עדכון סטטוס כל התורים של המשתמש שבוטלו
        DatabaseReference userAppointmentsRef = FirebaseDatabase.getInstance().getReference("appointments").child(phoneNumber);

        userAppointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot appointmentSnapshot : snapshot.getChildren()) {
                    appointmentSnapshot.getRef().child("status").setValue("🚫 בוטל עקב חופשה/מחלה");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "⚠ שגיאה בעדכון הסטטוס של התורים.", Toast.LENGTH_SHORT).show();
            }
        });
    }}
