package com.primemedia.marvels.fragments;

import static com.primemedia.marvels.Dashboard.bottomBar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.primemedia.marvels.R;


public class Search extends Fragment {
    FragmentManager fragmentManager;
    View view;
    FragmentTransaction fragmentTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);


        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle back press event here
                // For example, navigate to the previous fragment
                navigateToPreviousFragment();
            }
        };

        // Add the callback to the activity's OnBackPressedDispatcher
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return view;
    }

    private void navigateToPreviousFragment() {
        fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        Home homeFragment = new Home();
        fragmentTransaction.replace(R.id.contaner, homeFragment);
        fragmentTransaction.commit();
        bottomBar.setActiveItem(0);
        bottomBar.setBadge(0);
        bottomBar.removeBadge(1);
        bottomBar.setSelected(true);
    }
}