package fr.red.mviewer.utils;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class MovieGenre {

    private static Map<Integer, MovieGenre> genres = new HashMap<>();
    private static final MovieGenre unknown = new MovieGenre(-1, "Unknown");

    private int id;
    private String name;

    private MovieGenre(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addToCache() {
        if (!genres.containsKey(id)) {
            genres.put(id, this);
        }
    }

    public static void clearCache() {
        genres.clear();
    }

    public static MovieGenre getGenre(int genreId) {
        for (MovieGenre genre : genres.values()) {
            Log.d("_RED ", "Genre ID: " + genre.getId() + ", Name: " + genre.getName());
        }
        if (genres.containsKey(genreId)) {
            Log.d("_RED", "return " + genres.get(genreId).getName());
            return genres.get(genreId);
        } else {
            return null;
        }
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
