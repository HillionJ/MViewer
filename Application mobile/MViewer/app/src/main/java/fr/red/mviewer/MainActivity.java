package fr.red.mviewer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import fr.red.mviewer.tmdb.Movie;
import fr.red.mviewer.utils.MovieDB;

public class MainActivity extends AppCompatActivity {

    public LinearLayout flowLayout;
    private HorizontalScrollView scrollView;
    private GestureDetector gestureDetector;
    private int paddingX;
    private int currentIndex;

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        flowLayout = findViewById(R.id.idFlow);

        scrollView = findViewById(R.id.scrollView);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        currentIndex = 0;

        gestureDetector = new GestureDetector(this, new GestureListener());
        scrollView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        addPlaquette(null);

        scrollView.post(() -> {
            int screenWidth = scrollView.getWidth();
            int plaquetteWidth = flowLayout.getChildAt(0).getWidth();
            paddingX = (screenWidth - plaquetteWidth) / 2;
        });

        try {
            MovieDB.foo(this);
        } catch (Exception e) {
            Log.e("_RED", "Erreur : " + e.getMessage());
        }
    }

    public void addPlaquette(Movie movie) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View itemView = inflater.inflate(R.layout.item_flow, flowLayout, false);

        if (movie != null) {
            // Afficher le titre
            TextView titre = itemView.findViewById(R.id.idTitrePlaquette);
            titre.setText(movie.getTitle());

            // Charger l'affiche du film
            ImageView image = itemView.findViewById(R.id.idImagePlaquette);

            String posterUrl = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();
            Log.d("_RED", posterUrl);
            Glide.with(this)
                    .load(posterUrl)
                    .placeholder(R.drawable._1euctafoll)
                    .error(R.drawable._1euctafoll)
                    .into(image);
        }

        flowLayout.addView(itemView);
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(velocityX) > Math.abs(velocityY)) {
                    if (velocityX > 0) {
                        scrollToPrevious();
                    } else {
                        scrollToNext();
                    }
                    return true;
                }
                return false;
            }

    }

    // Méthode pour scroller automatiquement vers la prochaine plaquette
    private void scrollToNext() {
        scrollView.smoothScrollTo(getXToScroll(Math.min(flowLayout.getChildCount() - 1, currentIndex + 1)), 0);
        currentIndex = Math.min(flowLayout.getChildCount() - 1, currentIndex + 1);
    }

    // Méthode pour scroller automatiquement vers la plaquette précédente
    private void scrollToPrevious() {
        scrollView.smoothScrollTo(getXToScroll(Math.max(0, currentIndex - 1)), 0);
        currentIndex = Math.max(0, currentIndex - 1);
    }

    // Méthode pour ajuster automatiquement la position sur la plaquette la plus proche
    private void snapToNearestPlaquette() {
        int currentX = scrollView.getScrollX();
        int plaquetteWidth = flowLayout.getChildAt(0).getWidth();
        int nearestPlaquette = Math.round((float) currentX / plaquetteWidth);
        int targetX = nearestPlaquette * plaquetteWidth;
        scrollView.smoothScrollTo(targetX, 0);
    }

    private int getXToScroll(int targetID) {
        int x = -paddingX;
        for (int i = 0; i < targetID; i++) {
            x += flowLayout.getChildAt(i).getWidth();
        }
        Log.d("_RED", targetID + ": " + x);
        return x;
    }

    private View getCurrentPlaquette() {
        return getPlaquette(getCurrentPlaquetteID());
    }

    private View getPlaquette(int index) {
        return flowLayout.getChildAt(index);
    }

    private int getCurrentPlaquetteID() {
        int scrollX = scrollView.getScrollX();

        // Largeur de l'écran (zone visible du ScrollView)
        int screenWidth = scrollView.getWidth();

        // Initialiser les valeurs pour la recherche
        int closestIndex = -1;
        int closestDistance = Integer.MAX_VALUE;

        // Parcourir toutes les plaquettes du FlowLayout
        for (int i = 0; i < flowLayout.getChildCount(); i++) {
            View plaquette = flowLayout.getChildAt(i);

            // Calculer la position X de la plaquette
            int plaquetteLeft = plaquette.getLeft();
            int plaquetteRight = plaquette.getRight();

            // Calculer la distance entre le centre de la plaquette et le centre de l'écran
            int plaquetteCenter = (plaquetteLeft + plaquetteRight) / 2;
            int screenCenter = scrollX + (screenWidth / 2);

            int distance = Math.abs(plaquetteCenter - screenCenter);

            // Trouver la plaquette la plus proche du centre
            if (distance < closestDistance) {
                closestDistance = distance;
                closestIndex = i;
            }
        }

        if (closestIndex != -1) {
            return closestIndex;
        }
        return 0;
    }
}