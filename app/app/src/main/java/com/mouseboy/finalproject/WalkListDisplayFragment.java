package com.mouseboy.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.mouseboy.finalproject.server.Local;
import com.mouseboy.finalproject.server.ServerApi;
import com.mouseboy.finalproject.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Date;

public class WalkListDisplayFragment extends Fragment {

    ArrayList<ServerApi.WalkInfo> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.walk_list_display_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            Local.listWalks(
                requireContext(),
                new Date(0), new Date(Long.MAX_VALUE),
                e -> {
                    // TODO: import walk
                    items.addAll(Arrays.asList(e));
                },
                Util.toastFail(requireContext(), "Failed to Update Walk")
            );
        } else {
            items = (ArrayList<ServerApi.WalkInfo>) savedInstanceState.getSerializable("LocalWalks");
        }

        ListView listView = requireView().findViewById(R.id.walkListView);
        listView.setAdapter(new ArrayListAdaptor(requireContext(), items));
        listView.setOnItemClickListener(this::handle_click);
        listView.requestFocus();
    }


    void update_items() {
        ListView listView = requireView().findViewById(R.id.walkListView);
        ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("LocalWalks", items);
    }

    @Override
    public void onResume() {
        super.onResume();

        update_items();
    }

    void handle_click(AdapterView<?> parent, View item_views, int position, long id) {
        ServerApi.WalkInfo selectedItem = items.get(position);
        WalkDisplayFragment detailFragment = WalkDisplayFragment.newInstance(selectedItem, this);
        switch_to_fragment(detailFragment);
    }

    void switch_to_fragment(WalkDisplayFragment fragment){
        FragmentManager manager = requireActivity().getSupportFragmentManager();
        Fragment meetingDetailFragment = manager.findFragmentById(R.id.fragment_detail_container);
        if (meetingDetailFragment != null) {
            manager.popBackStack();
            manager.beginTransaction()
                .setCustomAnimations(0, android.R.anim.fade_out)
                .replace(R.id.fragment_detail_container, fragment)
                .addToBackStack(null)
                .commit();
        } else {
            manager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .add(R.id.fragment_detail_container, fragment)
                .addToBackStack(null)
                .commit();
        }
    }

    void update_walk(ServerApi.WalkInfo walk){
        for (int i = 0; i < items.size(); i++){
            if (Objects.equals(items.get(i).id, walk.id)){
                items.set(i, walk);
                break;
            }
        }
        update_items();
    }

    void delete_walk(ServerApi.WalkInfo walk){
        int index = -1;

        for (int i = 0; i < items.size(); i++){
            if (Objects.equals(items.get(i).id, walk.id)){
                index = i;
                break;
            }
        }

        if (index >= 0)
            items.remove(index);

        update_items();
    }

    void pop() {
        FragmentManager manager = requireActivity().getSupportFragmentManager();
        manager.popBackStack();

        update_items();
    }
}
