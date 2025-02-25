package com.example.queuemanagementapp.Adapters;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.queuemanagementapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.ViewHolder> {

    private List<String> appointmentsList;
    private String phoneNumber;
    private DatabaseReference databaseReference;
    private boolean isFirstClick = false;
    private Handler handler = new Handler();

    public AppointmentsAdapter(List<String> appointmentsList, String phoneNumber, DatabaseReference databaseReference) {
        this.appointmentsList = appointmentsList;
        this.phoneNumber = phoneNumber;
        this.databaseReference = databaseReference;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String appointmentKey = appointmentsList.get(position);
        holder.txtAppointment.setText("תור ב- " + appointmentKey.replace("_", " | שעה "));

        holder.btnCancel.setOnClickListener(v -> {
            if (!isFirstClick) {
                isFirstClick = true;
                Toast.makeText(v.getContext(), "לחץ פעם נוספת לאישור ביטול תור", Toast.LENGTH_SHORT).show();

                handler.postDelayed(() -> isFirstClick = false, 3000);
            } else {
                cancelAppointment(appointmentKey, position, holder.itemView);
                isFirstClick = false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointmentsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtAppointment;
        Button btnCancel;

        public ViewHolder(View itemView) {
            super(itemView);
            txtAppointment = itemView.findViewById(R.id.txt_appointment);
            btnCancel = itemView.findViewById(R.id.btn_cancel_appointment);
        }
    }

    /**
     * פונקציה למחיקת תור מה-Firebase.
     */
    private void cancelAppointment(String appointmentKey, int position, View view) {
        // שינוי פורמט המפתח כדי להתאים לשמות בפיירבייס
        String fixedAppointmentKey = appointmentKey.replace(" | שעה ", "_"); // הפיכת התצוגה לפורמט שמור בפיירבייס

        // הפנייה לנתיב המדויק בפיירבייס
        DatabaseReference appointmentRef = databaseReference.child(phoneNumber).child(fixedAppointmentKey);

        Log.d("FirebaseDelete", "🗑️ מנסה למחוק את התור בנתיב: " + appointmentRef.toString());

        // בדיקה אם התור קיים לפני המחיקה
        appointmentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("FirebaseCheck", "📌 נתונים שהתקבלו: " + snapshot.getValue());

                if (snapshot.exists()) {
                    Log.d("FirebaseDelete", "✅ התור נמצא, ממשיך למחיקה...");
                    appointmentRef.removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("FirebaseDelete", "✅ התור נמחק בהצלחה!");
                            Toast.makeText(view.getContext(), "התור בוטל בהצלחה!", Toast.LENGTH_SHORT).show();

                            // מחיקת התור גם מהרשימה בתצוגה
                            appointmentsList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, appointmentsList.size());
                        } else {
                            Log.e("FirebaseDelete", "❌ שגיאה בביטול התור!", task.getException());
                            Toast.makeText(view.getContext(), "שגיאה בביטול התור", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.d("FirebaseDelete", "⚠️ התור לא נמצא ב-Firebase! אולי הנתיב שגוי?");
                    Toast.makeText(view.getContext(), "⚠️ התור לא נמצא ב-Firebase!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseDelete", "❌ שגיאה בגישה ל-Firebase!", error.toException());
                Toast.makeText(view.getContext(), "שגיאה בגישה לנתוני Firebase!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
