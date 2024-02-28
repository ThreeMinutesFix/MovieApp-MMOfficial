package com.primemedia.studioflix;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.primemedia.studioflix.fragments.Accounts;
import com.primemedia.studioflix.fragments.tabitems.ComingSoon;
import com.primemedia.studioflix.fragments.tabitems.Home;
import com.primemedia.studioflix.fragments.Search;

import me.ibrahimsn.lib.NiceBottomBar;

public class Dashboard extends AppCompatActivity {
    FragmentTransaction fragmentTransaction;
    Home homeFragment;
    FragmentManager fragmentManager;
    public static NiceBottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        homeFragment = new Home();
        openHomeFragment(fragmentTransaction);

        bottomBar = findViewById(R.id.bottomNavigation);

        bottomBar.setOnItemSelected(integer -> {

            int selectedItemIndex = integer;
            FragmentTransaction newTransaction = fragmentManager.beginTransaction();
            if (selectedItemIndex == 0) {
                openHomeFragment(newTransaction);
            } else if (selectedItemIndex == 1) {
                openSearchFragment(newTransaction);
            } else if (selectedItemIndex == 2) {
                openComingFragment(newTransaction);
            } else if (selectedItemIndex == 3) {
                openSpaceFragment(newTransaction);
            }
            return null;
        });


    }

    private void openSearchFragment(FragmentTransaction newTransaction) {
        Search searchFragment = new Search();
        newTransaction.replace(R.id.contaner, searchFragment);
        newTransaction.commit();
    }


    private void openSpaceFragment(FragmentTransaction newTransaction) {
        Accounts searchFragment = new Accounts();
        newTransaction.replace(R.id.contaner, searchFragment);
        newTransaction.commit();
    }

    private void openComingFragment(FragmentTransaction transaction) {
        ComingSoon searchFragment = new ComingSoon();
        transaction.replace(R.id.contaner, searchFragment);
        transaction.commit();
    }


    private void openHomeFragment(FragmentTransaction fragmentTransaction) {
        Home homeFragment = new Home();
        fragmentTransaction.replace(R.id.contaner, homeFragment);
        fragmentTransaction.commit();
    }


}