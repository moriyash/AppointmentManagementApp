package com.example.queuemanagementapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.queuemanagementapp.Activities.MainActivity;
import com.example.queuemanagementapp.R;

public class AboutFragment extends Fragment {

    public AboutFragment() {
        // בנאי ריק (נדרש על ידי Android)
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        // כפתור חזרה לעמוד הראשי
        Button btnBackMain = view.findViewById(R.id.btn_back_main);
        btnBackMain.setOnClickListener(v -> {
            // יצירת Intent לעמוד הראשי (MainActivity)
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  // לוודא שכל ה-Activities הקודמים ייסגרו
            startActivity(intent); // הפעלת ה-Activity
            requireActivity().finish(); // מסיים את ה-Activity הנוכחי ומחזיר לעמוד הראשי
        });

        return view;
    }
}
