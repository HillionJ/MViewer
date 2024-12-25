package fr.red.mviewer.tmdb;

import java.util.List;

import fr.red.mviewer.utils.Movie;

public class MovieResponse {
    private int page;
    private List<Movie> results;

    public int getPage() {
        return page;
    }

    public List<Movie> getResults() {
        return results;
    }
}
