package com.example.queuemanagementapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.queuemanagementapp.Activities.MainActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.queuemanagementapp.R;

public class EnterPhoneFragment extends Fragment {
    private EditText edtPhoneNumber;
    private Button btnSavePhone;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;

    private static String lastPhoneNumber;

    public static String getLastPhoneNumber()
    {
        return lastPhoneNumber;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enter_phone, container, false);

        edtPhoneNumber = view.findViewById(R.id.edt_phone_number);
        btnSavePhone = view.findViewById(R.id.btn_send_code);
        progressBar = view.findViewById(R.id.progressBar);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        btnSavePhone.setOnClickListener(v -> {
            String phoneNumber = edtPhoneNumber.getText().toString().trim();

            if (!isValidPhoneNumber(phoneNumber)) {
                Toast.makeText(getActivity(), "אנא רשום מספר תקין", Toast.LENGTH_SHORT).show();
                return;
            }

            lastPhoneNumber = phoneNumber;
            phoneNumber = cleanPhoneNumber(phoneNumber);
            savePhoneNumber(phoneNumber, view);
        });

        Button btnBackHome = view.findViewById(R.id.btn_back_home);
        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }

    private void savePhoneNumber(String phoneNumber, View view) {
        progressBar.setVisibility(View.VISIBLE);

        databaseReference.child(phoneNumber).setValue(phoneNumber)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "מספר נשמר בהצלחה!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).navigate(R.id.action_enterPhoneFragment_to_selectServiceFragment);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "שגיאה בשמירת המספר", Toast.LENGTH_SHORT).show();
                });
    }

// validation
    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("^((\\+972|972|0)5[0-9]{8})$");
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