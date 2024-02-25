package com.primemedia.marvels.fragments;

import static com.primemedia.marvels.Dashboard.bottomBar;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
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
import com.primemedia.marvels.Constants;
import com.primemedia.marvels.R;
import com.primemedia.marvels.adapter.SearchListAdepter;
import com.primemedia.marvels.fragments.tabitems.Home;
import com.primemedia.marvels.list.SearchList;
import com.primemedia.marvels.utility.GospelUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Search extends Fragment {
    FragmentManager fragmentManager;
    View view;
    FragmentTransaction fragmentTransaction;
    AppCompatEditText searchTextview;
    int onlyPremium = 1;
    RecyclerView Top_results_view;
    LinearLayout searchResults,gnere_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);
        searchTextview = view.findViewById(R.id.searchTextview);
        Top_results_view = view.findViewById(R.id.Top_results_view);
        searchResults = view.findViewById(R.id.searchResults);
        gnere_layout = view.findViewById(R.id.common_layout);
        searchTextview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (String.valueOf(searchTextview.getText()).equals("")) {
                } else {

                    searchContent(String.valueOf(searchTextview.getText()));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Do nothing
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                navigateToPreviousFragment();
            }
        };

        // Add the callback to the activity's OnBackPressedDispatcher
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return view;
    }

    private void searchContent(String valueOf) {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        StringRequest sr = new StringRequest(Request.Method.GET, Constants.url + "searchContent/" + valueOf + "/" + onlyPremium, response -> {
            Log.d("IndianSwearch",response);
            if (!response.equals("No Data Avaliable")) {


                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<SearchList> searchList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();

                    String year = "";
                    if (!rootObject.get("release_date").getAsString().equals("")) {
                        year = GospelUtil.getYearFromDate(rootObject.get("release_date").getAsString());
                    }

                    String poster = rootObject.get("poster").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();
                    int contentType = rootObject.get("content_type").getAsInt();

                    if (status == 1) {
                        searchList.add(new SearchList(id, type, name, year, poster, contentType));
                    }
                }

                SearchListAdepter myadepter = new SearchListAdepter(requireContext(), searchList);
                Top_results_view.setLayoutManager(new GridLayoutManager(requireContext(), 3));
                Top_results_view.setAdapter(myadepter);
                myadepter.notifyDataSetChanged();
            } else {

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