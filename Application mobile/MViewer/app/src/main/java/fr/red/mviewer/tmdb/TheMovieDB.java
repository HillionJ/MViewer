package fr.red.mviewer.tmdb;


import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.red.mviewer.FlowActivity;
import fr.red.mviewer.SearchActivity;
import fr.red.mviewer.utils.IHM;
import fr.red.mviewer.utils.Movie;
import fr.red.mviewer.utils.MovieGenre;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class TheMovieDB {

    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String API_KEY = "9e7f180d2eb5afd903a8953b8d82dbea";
    private static final String LANG = "fr-FR";

    private static TheMovieDB instance = new TheMovieDB();
    private static retrofit2.Retrofit retrofit = null;
    public static TheMovieDB getInstance() {
        return instance;
    }

    private List<Movie> popular = new ArrayList<>();

    public TheMovieDB() {
        fetchGenres();
        fetchPoupular(1);
    }

    public void fetchGenres() {
        IRetrofitTheMovieDB api = getRetrofitInstance().create(IRetrofitTheMovieDB.class);
        api.getGenres(API_KEY, LANG).enqueue(new Callback<GenreResponse>() {
            @Override
            public void onResponse(Call<GenreResponse> call, Response<GenreResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MovieGenre> movieGenre = response.body().getGenres();
                    for (MovieGenre genre : movieGenre) {
                        genre.addToCache();
                    }
                    IHM ihm = IHM.getIHM();
                    FlowActivity flowActivity = (FlowActivity) ihm.getActivite(FlowActivity.class);
                }
            }

            @Override
            public void onFailure(Call<GenreResponse> call, Throwable t) {
                Log.e("_RED", "Erreur2 : " + t.getMessage());
            }
        });
    }

    public void fetchPoupular(int page) {
        IRetrofitTheMovieDB api = getRetrofitInstance().create(IRetrofitTheMovieDB.class);
        IHM ihm = IHM.getIHM();
        FlowActivity flowActivity = (FlowActivity) ihm.getActivite(FlowActivity.class);

        api.getPopularMovies(API_KEY, LANG, page).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getResults();
                    Collections.shuffle(movies);
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

    public void search(String query) {
        search(query, 1);
    }

    public void search(String query, int page) {
        IRetrofitTheMovieDB api = getRetrofitInstance().create(IRetrofitTheMovieDB.class);
        IHM ihm = IHM.getIHM();
        SearchActivity searchActivity = (SearchActivity) ihm.getActivite(SearchActivity.class);

        api.searchMovies(API_KEY, query, LANG, page).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                List<Movie> movies = response.body().getResults();
                searchActivity.setResults(movies);
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.e("_RED", "Erreur1 : " + t.getMessage());
            }
        });
    }

    public List<Movie> getPopular() {
        return popular;
    }

    private retrofit2.Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
