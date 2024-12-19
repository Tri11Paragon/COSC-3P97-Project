package com.mouseboy.finalproject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mouseboy.finalproject.server.Local;
import com.mouseboy.finalproject.server.ServerApi;

import java.util.ArrayList;

public class WalkDisplayFragment extends Fragment {

    private static final String ARG_WALK = "TheWalk";
    private final WalkListDisplayFragment list;
    private final ArrayList<ServerApi.WalkInfo> walks;

    WalkDisplayFragment(WalkListDisplayFragment list, ArrayList<ServerApi.WalkInfo> walks) {
        this.list = list;
        this.walks = walks;
    }

    public static WalkDisplayFragment newInstance(ServerApi.WalkInfo walk, WalkListDisplayFragment list, ArrayList<ServerApi.WalkInfo> walks) {
        WalkDisplayFragment fragment = new WalkDisplayFragment(list, walks);
        Bundle args = new Bundle();
        args.putSerializable(ARG_WALK, walk);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.walk_display_fragment, container, false);

        // Get the arguments passed to this fragment
        ServerApi.WalkInfo title = getArguments() != null ? (ServerApi.WalkInfo) getArguments().getSerializable(ARG_WALK) : null;

        if (title == null)
            return view;

        SeekBar numberSlider = view.findViewById(R.id.rating);
        numberSlider.setProgress((int) (title.rating * 100));
        numberSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ServerApi.WalkInfo lwalk = getArguments() != null ? (ServerApi.WalkInfo) getArguments().getSerializable(ARG_WALK) : null;

                if (lwalk == null)
                    return;

                Local.updateWalk(lwalk);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Set the data to the views
        ((EditText) view.findViewById(R.id.detailTitle)).setText(title.name);
        ((EditText) view.findViewById(R.id.detailTitle)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ServerApi.WalkInfo lwalk = getArguments() != null ? (ServerApi.WalkInfo) getArguments().getSerializable(ARG_WALK) : null;

                if (lwalk == null)
                    return;

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        ((EditText) view.findViewById(R.id.detailDescription)).setText(title.comment);
        ((TextView) view.findViewById(R.id.started)).setText(title.start.toString());
        ((TextView) view.findViewById(R.id.ended)).setText(title.end.toString());

        view.findViewById(R.id.deleteWalk).setOnClickListener(v -> {
            ServerApi.WalkInfo lwalk = getArguments() != null ? (ServerApi.WalkInfo) getArguments().getSerializable(ARG_WALK) : null;

            if (lwalk == null)
                return;

            walks.remove(lwalk);
            list.update_items();

            Local.deleteWalk(lwalk);
            list.pop();
        });

        return view;
    }
}
