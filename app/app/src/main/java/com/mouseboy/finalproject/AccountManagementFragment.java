package com.mouseboy.finalproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mouseboy.finalproject.server.ServerApi;
import com.mouseboy.finalproject.util.Util;


public class AccountManagementFragment extends Fragment {
    TextView name;

    public AccountManagementFragment() {

    }


    public static AccountManagementFragment newInstance() {
        AccountManagementFragment fragment = new AccountManagementFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_management, container, false);

        name = view.findViewById(R.id.name);
        view.findViewById(R.id.update_name).setOnClickListener(e -> ServerApi.updateUser(
            getContext(),
            new ServerApi.User(MainActivity.currentUser.id, name.getText().toString()),
            _void -> {
                MainActivity.currentUser.name = name.getText().toString();
                Util.toast(getContext(), "Display Name Updated");
            },
            Util.toastFail(getContext(), "Failed to Update Name")));
        name.setText(MainActivity.currentUser.name);

        view.findViewById(R.id.logout).setOnClickListener(e -> {
            MainActivity.user_logout(requireActivity());
            MainActivity.switchToNotLoggedIn(requireActivity());
        });
        view.findViewById(R.id.delete).setOnClickListener(e -> Util.showAlert(requireContext(),
            "Confirm", "Are you sure you want to delete this account and all its information?",
            () -> ServerApi.deleteUser(
                    requireContext(),
                    MainActivity.currentUser.id,
                    _void -> {
                        Util.toast(requireContext(), "Deleted Account");
                        MainActivity.user_logout(requireActivity());
                        MainActivity.switchToNotLoggedIn(requireActivity());
                    },
                    Util.toastFail(requireContext(), "Failed to Delete Account")
                  ),
            () -> {}
        ));
        return view;
    }
}
