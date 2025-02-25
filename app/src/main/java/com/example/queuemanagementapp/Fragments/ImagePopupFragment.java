package com.example.queuemanagementapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.queuemanagementapp.R;

public class ImagePopupFragment extends DialogFragment {
    private static final String ARG_IMAGE_RES = "image_res";

    public static ImagePopupFragment newInstance(int imageRes) {
        ImagePopupFragment fragment = new ImagePopupFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_IMAGE_RES, imageRes);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_popup, container, false);

        ImageView imgPopup = view.findViewById(R.id.imgPopup);
        ImageButton btnClose = view.findViewById(R.id.btnClose);

        if (getArguments() != null) {
            int imageRes = getArguments().getInt(ARG_IMAGE_RES);
            imgPopup.setImageResource(imageRes);
        }

        btnClose.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }
}
