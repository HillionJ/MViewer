package fr.red.mviewer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import fr.red.mviewer.tmdb.TheMovieDB;
import fr.red.mviewer.utils.GestureListener;
import fr.red.mviewer.utils.IHM;
import fr.red.mviewer.utils.LoadingQueue;
import fr.red.mviewer.utils.Movie;

public class SearchActivity extends AppCompatActivity {

    private static final int nbPlaquettesParLigne = 3;
    private static final double defaultPlaquette = 500.0 / 333.0; // 'Height / Width'

    private LinearLayout search_result;
    private TextView result_amount;
    private ScrollView scroll_result;
    private ArrayAdapter<String> adapter;
    private TheMovieDB theMovieDB = TheMovieDB.getInstance();
    private int plaquetteHeight, plaquetteWidth;
    private List<Movie> results = new ArrayList<>();
    private IHM ihm = IHM.getIHM();
    private List<LoadingQueue> queue = new ArrayList<>();
    private int currentQueueToken = 0;
    private boolean hasNextPage = false;
    private int currentPage = 1;
    private ImageView loadingImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ihm.ajouterIHM(this);
        ihm.applyDarkTheme();

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setIconified(false);

        search_result = findViewById(R.id.search_result);
        result_amount = findViewById(R.id.result_amount);
        result_amount.setVisibility(View.INVISIBLE);
        scroll_result = findViewById(R.id.scroll_result);
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
            //Vérifier si on arrive au bout du scroll
            if (scroll_result.getScrollY() == scroll_result.getChildAt(0).getHeight() - scroll_result.getHeight() && hasNextPage) {
                hasNextPage = false;
                currentPage++;
                theMovieDB.search(searchView.getQuery().toString(), currentPage);
            }
        },0, 1, TimeUnit.SECONDS);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            private ScheduledExecutorService scheduler;

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ScheduledExecutorService newScheduler = Executors.newScheduledThreadPool(1);
                scheduler = newScheduler;
                scheduler.schedule(() -> {
                    ihm.getActiviteActive().runOnUiThread(() -> {
                        if (this.scheduler == newScheduler && ihm.getActiviteActive().getClass() == SearchActivity.class) {
                            if (newText.isEmpty()) {
                                removeResultsUI();
                                result_amount.setVisibility(View.INVISIBLE);
                                hasNextPage = false;
                            } else {
                                if (search_result.getChildCount() == 0) {
                                    displayLoadingResultsUI();
                                }
                                theMovieDB.search(newText);
                            }
                        }
                    });
                }, 500, TimeUnit.MILLISECONDS);
                return false;
            }
        });

        removeResultsUI();
        search_result.post(() -> {
            plaquetteWidth = search_result.getWidth() / nbPlaquettesParLigne;
            plaquetteHeight = (int) (defaultPlaquette * (double) plaquetteWidth);
            updateResultsUI(0);
        });
    }

    public void removeResultsUI() {
        if (loadingImage != null) {
            loadingImage.clearAnimation();
        }
        while(search_result.getChildCount() > 0) {
            search_result.removeView(search_result.getChildAt(0));
        }
        queue.clear();
        loadingImage = null;
    }

    public void updateResultsUI(int startIndex) {
        if (currentPage == 1) {
            removeResultsUI();
        }
        int unclosedIndexes = (startIndex % nbPlaquettesParLigne);
        if (unclosedIndexes > 0) {
            LinearLayout row = (LinearLayout) search_result.getChildAt(search_result.getChildCount() - 1);
            for (int i = 0; i < nbPlaquettesParLigne - unclosedIndexes; i++) {
                row.addView(craftResult(results.get(startIndex + i)));
            }
            startIndex += nbPlaquettesParLigne;
        }
        for (int i = startIndex - unclosedIndexes; i < results.size(); i += nbPlaquettesParLigne) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = i; j < nbPlaquettesParLigne + i && j < results.size(); j++) {
                row.addView(craftResult(results.get(j)));
            }
            search_result.addView(row);
        }
    }

    public void displayLoadingResultsUI() {
        removeResultsUI();
        for (int i = 0; i < 10; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < nbPlaquettesParLigne; j++) {
                row.addView(craftResult(null));
            }
            search_result.addView(row);
        }
    }

    // Creer une image view et l'ajouter dans container
    public View craftResult(Movie movie) {
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (movie == null) {
            imageView.setImageResource(R.drawable.gray_background);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(plaquetteWidth, plaquetteHeight));
            ShimmerFrameLayout shimmerFrameLayout = new ShimmerFrameLayout(this);
            shimmerFrameLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            shimmerFrameLayout.setShimmer(
                    new com.facebook.shimmer.Shimmer.AlphaHighlightBuilder()
                            .setDuration(1000)
                            .setBaseAlpha(0.8f)
                            .setHighlightAlpha(1.0f)
                            .setDirection(com.facebook.shimmer.Shimmer.Direction.LEFT_TO_RIGHT)
                            .build()
            );
            shimmerFrameLayout.startShimmer();
            shimmerFrameLayout.addView(imageView);
            return shimmerFrameLayout;
        } else {
            imageView.setLayoutParams(new LinearLayout.LayoutParams(plaquetteWidth, plaquetteHeight));
            String posterUrl = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();
            queue.add(new LoadingQueue(posterUrl, imageView));
            return imageView;
        }
    }

    public void addResults(List<Movie> movies, int currentPage, int amount, boolean hasNextPage) {
        if (currentPage == 1) {
            this.results.clear();
            scroll_result.scrollTo(0, 0);
        }
        this.currentPage = currentPage;
        this.hasNextPage = hasNextPage;
        result_amount.setVisibility(View.VISIBLE);
        result_amount.setText(amount == 0 ? "Aucun résultat" : amount + " résultat" + (amount == 1 ? "" : "s"));
        int lastAmount = results.size();
        this.results.addAll(movies);
        if (loadingImage != null) {
            loadingImage.clearAnimation();
            search_result.removeView(loadingImage);
            loadingImage = null;
        }
        updateResultsUI(lastAmount);
        currentQueueToken++;
        loadNextInQueue();
        if (hasNextPage) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.loading);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,200, 0));
            imageView.setPadding(0, 50, 0, 50);
            imageView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.loading));
            loadingImage = imageView;
            search_result.addView(imageView);
        }
    }

    public void loadNextInQueue() {
        if (queue.isEmpty()) {
            return;
        }
        LoadingQueue loadingQueue = queue.remove(0);
        String posterUrl = loadingQueue.getPosterUrl();
        ImageView imageView = loadingQueue.getImageView();
        Glide.with(ihm.getActivite(SearchActivity.class))
                .load(posterUrl)
                .placeholder(R.drawable.gray_background)
                .error(R.drawable.gray_background)
                .into(imageView);
        int token = currentQueueToken;
        imageView.post(() -> {
            if (token == currentQueueToken) {
                loadNextInQueue();
            }
        });
    }

    public List<Movie> getResults() {
        return results;
    }
}
