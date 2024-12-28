package fr.red.mviewer.tmdb;


import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.red.mviewer.FlowActivity;
import fr.red.mviewer.SearchActivity;
import fr.red.mviewer.tmdb.response.GenreResponse;
import fr.red.mviewer.tmdb.response.MovieResponse;
import fr.red.mviewer.utils.IHM;
import fr.red.mviewer.utils.Movie;
import fr.red.mviewer.utils.MovieGenre;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class TheMovieDB {

    // Constantes
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String API_KEY = "9e7f180d2eb5afd903a8953b8d82dbea";
    private static final String LANG = "fr-FR";

    private static TheMovieDB instance = new TheMovieDB();
    private static retrofit2.Retrofit retrofit = null;
    public static TheMovieDB getInstance() {
        return instance;
    }

    private List<Movie> popular = new ArrayList<>();

    //Récupérer les genres et le flux à l'initialisation
    public TheMovieDB() {
        fetchGenres();
        fetchPoupular(1);
    }

    //Récupérer la liste des genre
    public void fetchGenres() {
        IRetrofitTheMovieDB api = getRetrofitInstance().create(IRetrofitTheMovieDB.class);
        api.getGenres(API_KEY, LANG).enqueue(new Callback<GenreResponse>() {
            @Override
            public void onResponse(Call<GenreResponse> call, Response<GenreResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MovieGenre> movieGenre = response.body().getGenres();
                    //Enregistrer dans la liste des genres
                    for (MovieGenre genre : movieGenre) {
                        genre.addToCache();
                    }
                }
            }

            @Override
            public void onFailure(Call<GenreResponse> call, Throwable t) {
                // TODO Prévoir un nouvel essai dès que la connexion revient
            }
        });
    }

    // Récupérer la liste des films populaires
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
                    // Mettre à jour la liste des films populaires
                    flowActivity.runOnUiThread(() -> {
                        popular.addAll(movies);
                        flowActivity.updateFlow();
                    });
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                // TODO Prévoir un nouvel essai dès que la connexion revient
            }
        });
    }

    // Rechercher les films correspondant au 'query'
    public void search(String query) {
        search(query, 1);
    }

    // Rechercher les films correspondant au 'query' et à la page 'page'
    public void search(String query, int page) {
        IRetrofitTheMovieDB api = getRetrofitInstance().create(IRetrofitTheMovieDB.class);
        IHM ihm = IHM.getIHM();
        SearchActivity searchActivity = (SearchActivity) ihm.getActivite(SearchActivity.class);

        api.searchMovies(API_KEY, query, LANG, page).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                List<Movie> movies = response.body().getResults();
                boolean hasNext = page < response.body().getTotal_pages();
                // Mettre à jour la liste des films en remplaçant ou en ajoutant
                searchActivity.runOnUiThread(() -> {
                    searchActivity.addResults(movies, page, response.body().getTotal_results(), hasNext);
                });
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                // TODO Afficher l'erreur
                // TODO Prévoir un nouvel essai dès que l'utilisateur redemande la recherche
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
