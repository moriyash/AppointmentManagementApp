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

public class AppointmentsListLoginFragment extends Fragment {

    private EditText edtPhoneNumber;
    private Button btnBackMain,btnViewAppointments;

    public AppointmentsListLoginFragment() {
        // קונסטרקטור ריק חובה
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_appointments, container, false);

        // אתחול רכיבים
        edtPhoneNumber = view.findViewById(R.id.edt_phone_number);
        btnViewAppointments = view.findViewById(R.id.btn_view_appointments);
        btnBackMain = view.findViewById(R.id.btn_back_main);

        // לחיצה על כפתור להצגת תורים
        btnViewAppointments.setOnClickListener(v -> {
            String phoneNumber = edtPhoneNumber.getText().toString().trim();

            if (phoneNumber.isEmpty()) {
                Toast.makeText(getContext(), "אנא הזן מספר טלפון", Toast.LENGTH_SHORT).show();
                return;
            }

            // הסרת קידומת כדי לוודא שהמספר נשמר כמו שצריך ב-Firebase
            phoneNumber = cleanPhoneNumber(phoneNumber);

            // ניווט לפרגמנט רשימת התורים עם מספר טלפון
            Bundle bundle = new Bundle();
            bundle.putString("phoneNumber", phoneNumber);
            Navigation.findNavController(v).navigate(R.id.action_myAppointmentsFragment_to_appointmentsListFragment, bundle);
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
