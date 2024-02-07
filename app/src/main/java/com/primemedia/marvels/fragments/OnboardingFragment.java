package com.primemedia.marvels.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.primemedia.marvels.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OnboardingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnboardingFragment extends Fragment {

    private static final String ARG_PAGE_NUMBER = "page_number";
    public OnboardingFragment() {

    }
    public static OnboardingFragment newInstance(int pageNumber) {
        OnboardingFragment fragment = new OnboardingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;

        // Inflate different layouts based on the page number
        int pageNumber = 0;
        if (getArguments() != null) {
            pageNumber = getArguments().getInt(ARG_PAGE_NUMBER);
        }
        switch (pageNumber) {
            case 1:
                view = inflater.inflate(R.layout.second_on_boarding_page, container, false);
                break;
            case 2:
                view = inflater.inflate(R.layout.third_on_boarding_page, container, false);
                break;
            case 3:
                view = inflater.inflate(R.layout.fourth_on_boarding_page, container, false);
                break;
            default:
                view = inflater.inflate(R.layout.first_on_boarding_page, container, false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(requireContext(), android.R.color.black));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().getWindow().setNavigationBarColor(ContextCompat.getColor(requireContext(), android.R.color.black));
        }
        return view;
    }
}