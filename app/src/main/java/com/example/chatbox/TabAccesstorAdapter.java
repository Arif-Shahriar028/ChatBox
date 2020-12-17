package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabAccesstorAdapter extends FragmentPagerAdapter {


    public TabAccesstorAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                chatFragment mchatFragment = new chatFragment();
                return mchatFragment;

            case 1:
                groupFragment mgroupFragment = new groupFragment();
                return mgroupFragment;

            case 2:
                contactsFragment mcontactFragment = new contactsFragment();
                return mcontactFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;  // 3 fragments
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Chats";

            case 1:
                return "Groups";

            case 2:
                return "Contacts";

            default:
                return null;
        }
    }
}
