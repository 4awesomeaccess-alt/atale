package com.example.newcardmaker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.newcardmaker.invite_sticker.invite_sticker_fragment;
import com.example.newcardmaker.invite_sticker.invite_sticker_fragment_all;
import com.example.newcardmaker.invite_sticker.invite_sticker_fragment_common;

public class invite_ViewPagerAdapter_sticker extends FragmentStateAdapter {

    public invite_ViewPagerAdapter_sticker(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new invite_sticker_fragment();
            case 1:
                return new invite_sticker_fragment_common();
            case 2:
                return new invite_sticker_fragment_all();
            default:
                return new invite_sticker_fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;

    }
}
