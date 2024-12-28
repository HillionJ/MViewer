package fr.red.mviewer.tmdb.response;

import java.util.List;

import fr.red.mviewer.utils.Movie;

public class MovieResponse {
    private int page;
    private int total_pages;
    private int total_results;
    private List<Movie> results;

    public int getPage() {
        return page;
    }

    public List<Movie> getResults() {
        return results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public int getTotal_results() {
        return total_results;
    }
}
