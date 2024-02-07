package com.primemedia.marvels;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.primemedia.marvels.adapter.AllGenreListAdepter;
import com.primemedia.marvels.fragments.Accounts;
import com.primemedia.marvels.fragments.ComingSoon;
import com.primemedia.marvels.fragments.Home;
import com.primemedia.marvels.fragments.MarvelPage;
import com.primemedia.marvels.fragments.ParaVeresal;
import com.primemedia.marvels.fragments.Warner;
import com.primemedia.marvels.list.GenreList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.ibrahimsn.lib.NiceBottomBar;

public class Dashboard extends AppCompatActivity {
    View tabcustom;
    public static View genre_initlizer;
    TextView movies, warner,  more_items,paramount;
    FragmentTransaction fragmentTransaction;
    Home homeFragment;
    FragmentManager fragmentManager;
    public RecyclerView categories;
    LinearLayout closebtn;
    NiceBottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        tabcustom = findViewById(R.id.tabcustom);
        movies = tabcustom.findViewById(R.id.movies);
        warner = tabcustom.findViewById(R.id.warner);

        paramount = tabcustom.findViewById(R.id.paramount);
        more_items = tabcustom.findViewById(R.id.more_items);
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
                openComingFragment(newTransaction);
            } else if (selectedItemIndex == 4) {
                openSpaceFragment(newTransaction);
            }
            return null;
        });
        movies.setOnClickListener(v ->
        {
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction newTransaction = fragmentManager.beginTransaction();
            OpenMoviesFrag(newTransaction);
        });
        paramount.setOnClickListener(v ->
        {
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction newTransaction = fragmentManager.beginTransaction();
            ParaFragment(newTransaction);
        });
        warner.setOnClickListener(v ->
        {
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction newTransaction = fragmentManager.beginTransaction();
            WarnerFragment(newTransaction);
        });
        genre_initlizer = findViewById(R.id.genre_initlizer);

        more_items.setOnClickListener(v ->
        {
            LoadGenre();
        });
        closebtn = genre_initlizer.findViewById(R.id.closebtn);
        closebtn.setOnClickListener(v ->
        {
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(genre_initlizer, "alpha", 1f, 0f);
            fadeOut.setDuration(500); // Adjust the duration as needed
            fadeOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    genre_initlizer.setVisibility(View.GONE);
                }
            });
            fadeOut.start();
        });
    }

    private void ParaFragment(FragmentTransaction newTransaction) {
        ParaVeresal paraVersePages = new ParaVeresal();
        newTransaction.replace(R.id.contaner, paraVersePages);
        newTransaction.commit();
    }

    private void WarnerFragment(FragmentTransaction newTransaction)
    {
        Warner warnerPages = new Warner();
        newTransaction.replace(R.id.contaner, warnerPages);
        newTransaction.commit();
    }

    private void OpenMoviesFrag(FragmentTransaction newTransaction) {
        MarvelPage marvelPage = new MarvelPage();
        newTransaction.replace(R.id.contaner, marvelPage);
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

    private void LoadGenre() {

        categories = genre_initlizer.findViewById(R.id.categories);
        List<GenreList> genreList = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, Constants.url + "getGenreList", response -> {
            if (!response.equals("No Data Avaliable")) {
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(genre_initlizer, "alpha", 0f, 1f);
                fadeIn.setDuration(500);
                fadeIn.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        genre_initlizer.setVisibility(View.VISIBLE);
                    }
                });
                fadeIn.start();
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();
                    String icon = rootObject.get("icon").getAsString();
                    String description = rootObject.get("description").getAsString();
                    int featured = rootObject.get("featured").getAsInt();
                    int status = rootObject.get("status").getAsInt();
                    if (status == 1) {
                        genreList.add(new GenreList(id, name, icon, description, featured, status));
                    }
                }
                AllGenreListAdepter myadepter = new AllGenreListAdepter(this, genreList);

                categories.setLayoutManager(new GridLayoutManager(this, 1, RecyclerView.VERTICAL, false));
                categories.setAdapter(myadepter);
            } else {

                genre_initlizer.setVisibility(View.GONE);
            }

        }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };
        queue.add(sr);
    }

    private void openHomeFragment(FragmentTransaction fragmentTransaction) {
        Home homeFragment = new Home();
        fragmentTransaction.replace(R.id.contaner, homeFragment);
        fragmentTransaction.commit();
    }

    public View genre_initlizer() {
        return genre_initlizer;
    }

    public View tabcustom() {
        return tabcustom;
    }
}