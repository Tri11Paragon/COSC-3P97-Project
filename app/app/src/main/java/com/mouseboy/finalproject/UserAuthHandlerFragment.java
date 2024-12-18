package com.mouseboy.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class UserAuthHandlerFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_auth_handler_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnLogin = view.findViewById(R.id.btn_login);
        Button btnRegister = view.findViewById(R.id.btn_register);
        Button btnContinue = view.findViewById(R.id.no_account);

        // Show Login Dialog when login button is clicked
        btnLogin.setOnClickListener(v -> {
            MainActivity.pushFragment(getActivity(), new LoginFragment());
        });

        // Show Registration Dialog when register button is clicked
        btnRegister.setOnClickListener(v -> {
            MainActivity.pushFragment(getActivity(), new RegistrationFragment());
        });

        btnContinue.setOnClickListener(v -> {
            MainActivity.pushFragment(getActivity(), new MainFragment());
        });
    }

}
