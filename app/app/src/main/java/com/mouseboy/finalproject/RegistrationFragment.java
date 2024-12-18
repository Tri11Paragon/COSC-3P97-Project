package com.mouseboy.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.mouseboy.finalproject.server.ServerApi;
import com.mouseboy.finalproject.util.OkHttp;
import com.mouseboy.finalproject.util.Util;

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
        TextView displayInput = view.findViewById(R.id.displayname);
        TextView usernameInput = view.findViewById(R.id.username);
        TextView passwordInput = view.findViewById(R.id.password);
        submitButton.setOnClickListener(v -> {
            String display = displayInput.getText().toString().trim();
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            String mash = Util.mash(username, password);
            ServerApi.User user = new ServerApi.User(mash, display);
            ServerApi.createUser(getContext(), user, _void -> {
                Util.toast(getContext(), "Account Creation Successful");
                MainActivity.user_registered(requireActivity(), username, password);
                MainActivity.receiveUserData(user);
                MainActivity.switchToUserHome(requireActivity());
                dismiss();
            }, error -> {
                Util.logThrowable(error);
                if(error instanceof OkHttp.HttpException){
                    Toast.makeText(getContext(), "Already exists", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getTheme() {
        return com.google.android.material.R.style.Theme_Material3_Dark_Dialog;
    }

    static void open_registration(FragmentActivity activity){
        RegistrationFragment registrationFragment = new RegistrationFragment();
//        registrationFragment.setCancelable(false);
//        registrationFragment.show(manager, "RegistrationFragment");
        MainActivity.pushFragment(activity, registrationFragment);
    }
}
