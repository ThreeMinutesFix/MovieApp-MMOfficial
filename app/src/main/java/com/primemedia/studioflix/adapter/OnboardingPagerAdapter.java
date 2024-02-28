package com.primemedia.studioflix.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.primemedia.studioflix.fragments.OnboardingFragment;


public class OnboardingPagerAdapter extends FragmentPagerAdapter {
    public OnboardingPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return OnboardingFragment.newInstance(position);
    }
    @Override
    public int getCount() {
        return 4;
    }
}
