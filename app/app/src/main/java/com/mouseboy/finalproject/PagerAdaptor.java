package com.mouseboy.finalproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mouseboy.finalproject.server.Local;

public class PagerAdaptor extends FragmentStateAdapter {

    public PagerAdaptor(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new WalkListDisplayFragment();
            case 1:
                return HomePage.newInstance();
            case 2:
                if(Local.isUserLoggedIn())
                    return AccountManagementFragment.newInstance();
                else
                    return UserAuthHandlerFragment.newInstance();
        }
        return HomePage.newInstance();
    }

    @Override
    public int getItemCount() {
        return 3;
    }

}
