package com.example.marketer10;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlacesResponse {
    List<Market> results;
    public List<Market> getResults() {
        return results;
    }
}
