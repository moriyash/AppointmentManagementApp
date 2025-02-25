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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.queuemanagementapp.Activities.MainActivity;
import com.example.queuemanagementapp.R;

public class AdminLoginFragment extends Fragment {

    private EditText emailEditText, passwordEditText;
    private Button loginButton, backButton;
    private NavController navController;

    // אימייל וסיסמה מורשים
    private static final String ADMIN_EMAIL = "moriya2002@gmail.com";
    private static final String ADMIN_PASSWORD = "hair1234";

    public AdminLoginFragment() {
        // קונסטרקטור ריק חובה
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_login, container, false);

        // אתחול NavController
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_admin);

        emailEditText = view.findViewById(R.id.et_admin_email);
        passwordEditText = view.findViewById(R.id.et_admin_password);
        loginButton = view.findViewById(R.id.btn_admin_login_confirm);
        backButton = view.findViewById(R.id.btn_main_back);

        // מאזין לכפתור ההתחברות
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
                Toast.makeText(getContext(), "התחברות הצליחה!", Toast.LENGTH_SHORT).show();

                // ניווט ללוח השנה
                navController.navigate(R.id.action_adminLoginFragment_to_adminCalendarFragment);

            } else {
                Toast.makeText(getContext(), "שגיאה: אימייל או סיסמה שגויים!", Toast.LENGTH_SHORT).show();
            }
        });

        // חזרה לעמוד הראשי
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  // חזרה לעמוד הראשי עם סגירת הפרגמנט הנוכחי
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }
}
