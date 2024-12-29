package fr.red.mviewer.utils;

import android.widget.ImageView;

public class LoadingQueue {
    private String posterUrl;
    private ImageView imageView;

    public LoadingQueue(String posterUrl, ImageView imageView) {
        this.posterUrl = posterUrl;
        this.imageView = imageView;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public ImageView getImageView() {
        return imageView;
    }
}
