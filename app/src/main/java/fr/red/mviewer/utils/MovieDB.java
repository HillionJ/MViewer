package fr.red.mviewer.utils;


import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import fr.red.mviewer.MainActivity;
import fr.red.mviewer.tmdb.ApiClient;
import fr.red.mviewer.tmdb.Movie;
import fr.red.mviewer.tmdb.MovieResponse;
import fr.red.mviewer.tmdb.TheMovieDB;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDB {

    public static void foo(MainActivity activity) {
        TheMovieDB api = ApiClient.getRetrofitInstance().create(TheMovieDB.class);
        String apiKey = "9e7f180d2eb5afd903a8953b8d82dbea"; // Remplacez par votre clé API
        String language = "fr-FR"; // Pour obtenir les données en français

        api.getPopularMovies(apiKey, language).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getResults();
                    Collections.shuffle(movies);
                    //Récupérer les films un par un
                    activity.runOnUiThread(() -> {
                        for (Movie movie : movies) {
                            activity.addPlaquette(movie);
                        }
                        activity.flowLayout.removeView(activity.flowLayout.getChildAt(0));
                    });
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.e("_RED", "Erreur : " + t.getMessage());
            }
        });
    }

}
