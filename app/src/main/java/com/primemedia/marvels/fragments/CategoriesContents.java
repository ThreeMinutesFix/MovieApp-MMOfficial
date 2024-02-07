package com.primemedia.marvels.fragments;

import static com.primemedia.marvels.Dashboard.genre_initlizer;
import static com.primemedia.marvels.fragments.Home.setWindowFlag;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.primemedia.marvels.Dashboard;
import com.primemedia.marvels.R;
import com.primemedia.marvels.adapter.AllGenreListAdepter;

import java.util.List;


public class CategoriesContents extends Fragment {
    View view;
    Context mContext;
    View customs_tab;
    CardView closepoint;
    AllGenreListAdepter genreListAdepter;
    private List<String> genreData;
    private int selectedPosition = RecyclerView.NO_POSITION;
    public RecyclerView genreRecyclerview;
    FragmentManager fragmentManager;
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setStatusBar();
        view = inflater.inflate(R.layout.fragment_catories_contents, container, false);
        Dashboard dashboard = (Dashboard) requireActivity();
        View moreItemsTextView = dashboard.genre_initlizer();
        View tabCustom = dashboard.tabcustom();

        moreItemsTextView.setVisibility(View.GONE);
        tabCustom.setVisibility(View.GONE);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(genre_initlizer, "alpha", 1f, 0f);
        fadeOut.setDuration(500);
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                genre_initlizer.setVisibility(View.GONE);
            }
        });
        fadeOut.start();

        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setWindowFlag(requireActivity(), WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        requireActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);

        Bundle bundle = getArguments();
        if (bundle != null) {
            int id = bundle.getInt("ID", -1);
            String genres = bundle.getString("Name", "");
            int selectedPosition = bundle.getInt("SelectedPosition", -1);

            customs_tab = view.findViewById(R.id.customs);
            TextView genre_selected = customs_tab.findViewById(R.id.genre_selected);
            genre_selected.setText(genres);

        }
        closepoint = view.findViewById(R.id.closepoint);
        closepoint.setOnClickListener(v ->
        {
            fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction newTransaction = fragmentManager.beginTransaction();
            OpenCloseHome(newTransaction);
        });
        return view;

    }

    private void OpenCloseHome(FragmentTransaction newTransaction) {
        Home homeFragment = new Home();
        newTransaction.replace(R.id.contaner, homeFragment);
        newTransaction.commit();
    }


    private void setStatusBar() {
        Window window = requireActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        {
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;

    }
}
