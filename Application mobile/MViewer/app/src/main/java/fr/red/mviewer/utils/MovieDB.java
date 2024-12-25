package fr.red.mviewer.utils;


import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.red.mviewer.FlowActivity;
import fr.red.mviewer.tmdb.APIClient;
import fr.red.mviewer.tmdb.Movie;
import fr.red.mviewer.tmdb.MovieResponse;
import fr.red.mviewer.tmdb.TheMovieDB;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDB {

    private static MovieDB instance = new MovieDB();
    private List<Movie> popular = new ArrayList<>();

    public static MovieDB getInstance() {
        return instance;
    }

    public MovieDB() {
        fetchPoupular();
    }

    public void fetchPoupular() {
        TheMovieDB api = APIClient.getRetrofitInstance().create(TheMovieDB.class);
        String apiKey = "9e7f180d2eb5afd903a8953b8d82dbea";
        String language = "fr-FR";
        FlowActivity flowActivity = (FlowActivity) IHM.getIHM().getActivite(FlowActivity.class);


        api.getPopularMovies(apiKey, language).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getResults();
                    Collections.shuffle(movies);
                    //Récupérer les films un par un
                    flowActivity.runOnUiThread(() -> {
                        popular.addAll(movies);
                        flowActivity.updateFlow();
                    });
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.e("_RED", "Erreur0 : " + t.getMessage());
            }
        });
    }

    public List<Movie> getPopular() {
        return popular;
    }
}
