package fr.red.mviewer.widgets;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;

import fr.red.mviewer.FlowActivity;
import fr.red.mviewer.R;
import fr.red.mviewer.utils.Movie;
import fr.red.mviewer.utils.GestureListener;
import fr.red.mviewer.utils.IHM;
import fr.red.mviewer.tmdb.TheMovieDB;

public class FlowWidget {

    public LinearLayout flowLayout;
    private HorizontalScrollView scrollView;
    private GestureDetector gestureDetector;
    private int paddingX;
    private int currentIndex;
    private IHM ihm = IHM.getIHM();

    @SuppressLint("ClickableViewAccessibility")
    public FlowWidget(LinearLayout flowLayout, HorizontalScrollView scrollView) {
        this.flowLayout = flowLayout;
        this.scrollView = scrollView;
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        this.gestureDetector = new GestureDetector(ihm.getActiviteActive(), new GestureListener());
        scrollView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        updateFlow();

        scrollView.post(() -> {
            int screenWidth = scrollView.getWidth();
            int plaquetteWidth = flowLayout.getChildAt(0).getWidth();
            paddingX = (screenWidth - plaquetteWidth) / 2;
            currentIndex = getCurrentPlaquetteID();
        });
    }

    public void addPlaquette(Movie movie) {
        LayoutInflater inflater = LayoutInflater.from(ihm.getActivite(FlowActivity.class));
        View itemView;

        if (movie != null) {
            itemView = inflater.inflate(R.layout.item_flow, flowLayout, false);
            // Afficher le titre
            TextView titre = itemView.findViewById(R.id.idTitrePlaquette);
            titre.setText(movie.getTitle());

            // Charger l'affiche du film
            ImageView image = itemView.findViewById(R.id.idImagePlaquette);

            String posterUrl = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();
            Glide.with(ihm.getActivite(FlowActivity.class))
                    .load(posterUrl)
                    .placeholder(R.drawable._1euctafoll)
                    .error(R.drawable._1euctafoll)
                    .into(image);
        } else {
            itemView = inflater.inflate(R.layout.item_flow_loading, flowLayout, false);
            ShimmerFrameLayout shimmerFrameLayout = itemView.findViewById(R.id.shimmerLayout);

            shimmerFrameLayout.startShimmer();
        }

        flowLayout.addView(itemView);
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

    public void scrollToNext() {
        scrollView.smoothScrollTo(getXToScroll(Math.min(flowLayout.getChildCount() - 1, currentIndex + 1)), 0);
        currentIndex = Math.min(flowLayout.getChildCount() - 1, currentIndex + 1);
    }

    // Méthode pour scroller automatiquement vers la plaquette précédente
    public void scrollToPrevious() {
        scrollView.smoothScrollTo(getXToScroll(Math.max(0, currentIndex - 1)), 0);
        currentIndex = Math.max(0, currentIndex - 1);
    }

    public void removeLoadingWidgets() {
        for (int i = 0; i < flowLayout.getChildCount(); i++) {
            View v = flowLayout.getChildAt(i);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && R.layout.item_flow_loading == v.getSourceLayoutResId()) {
                flowLayout.removeView(v);
                i--;
            }
        }
    }

    public void setFlowLayout(LinearLayout flowLayout) {
        this.flowLayout = flowLayout;
    }

    public void setScrollView(HorizontalScrollView scrollView) {
        this.scrollView = scrollView;
    }

    public void updateFlow() {
        if (TheMovieDB.getInstance().getPopular().isEmpty()) {
            addPlaquette(null);
            addPlaquette(null);
            addPlaquette(null);
            addPlaquette(null);
            addPlaquette(null);
            addPlaquette(null);
        } else {
            for (Movie movie : TheMovieDB.getInstance().getPopular()) {
                addPlaquette(movie);
            }
            View lastPlaquette = flowLayout.getChildAt(flowLayout.getChildCount() - 1);
            lastPlaquette.post(this::removeLoadingWidgets);
        }
    }

    public void hide() {
        scrollView.setVisibility(View.INVISIBLE);
        Log.d("_RED", "hide");
    }

    public void show() {
        scrollView.setVisibility(View.VISIBLE);
        Log.d("_RED", "show");
    }

    public boolean isVisible() {
        return scrollView.getVisibility() == View.VISIBLE;
    }
}