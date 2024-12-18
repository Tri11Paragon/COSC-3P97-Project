package com.mouseboy.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.mouseboy.finalproject.server.Local;
import com.mouseboy.finalproject.server.ServerApi;
import com.mouseboy.finalproject.util.Util;

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

            String mash = Util.mash(username, password);
            ServerApi.getUser(getContext(), mash, user -> {
                Util.toast(getContext(), "Login Successful");
                MainActivity.user_registered(requireActivity(), username, password);
                Local.login(user);
                MainActivity.switchToUserHome(requireActivity());
                dismiss();
            }, Util.toastFail(getContext(), "Invalid Login"));
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
