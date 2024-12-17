package com.mouseboy.finalproject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

public class LoginFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the login layout
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get input fields
        EditText usernameInput = view.findViewById(R.id.input_username);
        EditText passwordInput = view.findViewById(R.id.input_password);
        Button loginButton = view.findViewById(R.id.btn_login);

        // Handle login logic
        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Mock validation for simplicity
            if (!username.isEmpty() && !password.isEmpty()) {
                // Successful login: Show a message or perform your logic
                Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                MainActivity.user_registered(requireActivity(), "Todo", "Todo");
                MainActivity.switch_to_main(requireActivity());

                // Dismiss the dialog
                dismiss();
            } else {
                // Error case
                Toast.makeText(getContext(), "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getTheme() {
        return com.google.android.material.R.style.Theme_Material3_Dark_Dialog;
    }

    static void open_login(FragmentActivity activity){
        LoginFragment loginFragment = new LoginFragment();
//        loginFragment.setCancelable(false);
//        loginFragment.show(manager, "LoginFragment");
        MainActivity.switchToFragment(activity, loginFragment);
    }
}
