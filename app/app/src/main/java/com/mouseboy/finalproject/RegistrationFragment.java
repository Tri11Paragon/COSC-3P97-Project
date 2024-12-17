package com.mouseboy.finalproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class RegistrationFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate your custom layout
        return inflater.inflate(R.layout.registration_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Handle registration completion
        Button submitButton = view.findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(v -> {
            // Mark registration as complete
            MainActivity.user_registered(requireActivity(), "Todo", "Todo");
            MainActivity.switch_to_main(requireActivity());

            // Dismiss the dialog
            dismiss();
        });
    }

    @Override
    public int getTheme() {
        return com.google.android.material.R.style.Theme_Material3_Dark_Dialog;
    }

    static void open_registration(FragmentManager manager){
        RegistrationFragment registrationFragment = new RegistrationFragment();
        registrationFragment.setCancelable(false);
        registrationFragment.show(manager, "RegistrationFragment");
    }
}
