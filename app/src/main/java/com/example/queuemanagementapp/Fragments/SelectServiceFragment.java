package com.example.queuemanagementapp.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.queuemanagementapp.R;
import java.util.Arrays;
import java.util.List;

public class SelectServiceFragment extends Fragment {
    private List<String> services;
    private String selectedService = "";
    private EditText editTextOtherService;

    public SelectServiceFragment() {
        // אתחול רשימת הטיפולים
        services = Arrays.asList(
                "✂️ תספורת גברים - ₪70",
                "💇‍♀️ תספורת נשים - ₪120",
                "🎨 צביעת שיער - ₪200",
                "🧴 החלקת שיער קרטין - ₪450",
                "💆‍♀️ טיפולי שיקום שיער - ₪300",
                "🖌 גוונים לשיער קצר - ₪250",
                "🖌 גוונים לשיער ארוך - ₪350",
                "💆‍♂️ שטיפת צבע - ₪100",
                "🪮 עיצוב תסרוקות ערב - ₪300"
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_service, container, false);

        RadioGroup radioGroup = view.findViewById(R.id.radio_group_services);
        Button btnNext = view.findViewById(R.id.btn_next);
        Button btnBack = view.findViewById(R.id.btn_back);
        editTextOtherService = view.findViewById(R.id.edit_text_other_service);
        editTextOtherService.setVisibility(View.GONE); // תחילה מוסתר

        // יצירת כפתורי בחירה דינאמית
        for (String service : services) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(service);
            radioButton.setLayoutParams(new RadioGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, // גורם לכפתורים להיות רחבים
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            radioGroup.addView(radioButton);
        }

        // הוספת אפשרות "אחר"
        RadioButton radioButtonOther = new RadioButton(getContext());
        radioButtonOther.setText("אחר...");
        radioGroup.addView(radioButtonOther);

        // קבלת השירות שנבחר
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = view.findViewById(checkedId);
            if (selectedRadioButton != null) {
                if (selectedRadioButton == radioButtonOther) {
                    editTextOtherService.setVisibility(View.VISIBLE); // הצגת התיבה
                    selectedService = editTextOtherService.getText().toString();
                } else {
                    editTextOtherService.setVisibility(View.GONE);
                    selectedService = selectedRadioButton.getText().toString();
                }
            }
        });

        // מעקב אחר שינויים בתיבת הטקסט
        editTextOtherService.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (radioButtonOther.isChecked()) {
                    selectedService = s.toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // לחיצה על "המשך" - שליחת הנתון לדף הבא
        btnNext.setOnClickListener(v -> {
            if (!selectedService.isEmpty()) {
                Bundle bundle = new Bundle();
                bundle.putString("selectedService", selectedService);
                Navigation.findNavController(view).navigate(R.id.action_selectServiceFragment_to_selectDateFragment, bundle);
            }
        });

        // לחיצה על "חזור"
        btnBack.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());

        return view;
    }
}
