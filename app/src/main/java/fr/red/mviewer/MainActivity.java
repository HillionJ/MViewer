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

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private LinearLayout flowLayout;
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

        addPlaquette(flowLayout, "1");
        addPlaquette(flowLayout, "2");
        addPlaquette(flowLayout, "3");
        addPlaquette(flowLayout, "4");
        addPlaquette(flowLayout, "5");
        addPlaquette(flowLayout, "6");

        gestureDetector = new GestureDetector(this, new GestureListener());
        scrollView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        scrollView.post(() -> {
            int screenWidth = scrollView.getWidth();
            int plaquetteWidth = flowLayout.getChildAt(0).getWidth();
            paddingX = (screenWidth - plaquetteWidth) / 2;
        });
    }

    private void addPlaquette(LinearLayout parentLayout, String text) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View itemView = inflater.inflate(R.layout.item_flow, parentLayout, false);

        TextView itemText = itemView.findViewById(R.id.idTitrePlaquette);
        itemText.setText(text);

        parentLayout.addView(itemView);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.d("_RED", "onScroll()");
                return false;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // Gère les mouvements rapides
                Log.d("_RED", "onFling()");
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
        currentIndex++;
    }

    // Méthode pour scroller automatiquement vers la plaquette précédente
    private void scrollToPrevious() {
        scrollView.smoothScrollTo(getXToScroll(Math.max(0, currentIndex - 1)), 0);
        currentIndex--;
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