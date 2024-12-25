class MovieDB
!!!140162.java!!!	foo(inout activity : MainActivity) : void
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
