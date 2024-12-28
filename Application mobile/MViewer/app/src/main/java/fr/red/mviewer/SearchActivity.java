package fr.red.mviewer;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Color;
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
import android.widget.AutoCompleteTextView;
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

    private static final int nbPlaquettesParLigne_LANDSCAPE = 5;
    private static final int nbPlaquettesParLigne_PORTRAIT = 3;
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
    private int nbPlaquettesParLigne = 3;

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

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.nbPlaquettesParLigne = nbPlaquettesParLigne_LANDSCAPE;
        } else {
            this.nbPlaquettesParLigne = nbPlaquettesParLigne_PORTRAIT;
        }
        ihm.ajouterIHM(this);
        ihm.applyDarkTheme();

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setIconified(false);

        search_result = findViewById(R.id.search_result);
        result_amount = findViewById(R.id.result_amount);
        result_amount.setVisibility(View.INVISIBLE);
        scroll_result = findViewById(R.id.scroll_result);

        //Vérifier constament si on arrive au bout de la liste des résultats pour afficher les suivantes
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
                        //Vérifier si le query n'a pas changé depuis 0.5s pour effectuer la recherche.
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
        // Calculer la taille des plaquette en fonction de la taille du conteneur
        search_result.post(() -> {
            plaquetteWidth = search_result.getWidth() / nbPlaquettesParLigne;
            plaquetteHeight = (int) (defaultPlaquette * (double) plaquetteWidth);
            updateResultsUI(0);
        });
    }

    // Supprimer tout le contenu des résultats
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

    // Mettre à jour le flux
    public void updateResultsUI(int startIndex) {
        if (currentPage == 1) {
            // Supprimer le flux actuelle si c'est des nouveaux résultats
            removeResultsUI();
        }
        // Vérifier en cas de nouveaux résulats, le nombre de plaquettes manquantes dans les LinearLayout
        int unclosedIndexes = (startIndex % nbPlaquettesParLigne);
        if (unclosedIndexes > 0) {
            // En cas de lignes existantes non remplient au max,
            // les remplir avec les nouveaux résultats
            LinearLayout row = (LinearLayout) search_result.getChildAt(search_result.getChildCount() - 1);
            for (int i = 0; i < nbPlaquettesParLigne - unclosedIndexes; i++) {
                row.addView(craftResult(results.get(startIndex + i)));
            }
            startIndex += nbPlaquettesParLigne;
        }
        // Ajouter les lignes manquantes pour ajouter nbPlaquettesParLigne dedans
        for (int i = startIndex - unclosedIndexes; i < results.size(); i += nbPlaquettesParLigne) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = i; j < nbPlaquettesParLigne + i && j < results.size(); j++) {
                row.addView(craftResult(results.get(j)));
            }
            search_result.addView(row);
        }
    }

    // Afficher 10 lignes de plaquettes qui chargement en cas de nouvelle recherche
    // Et que les résultats sont vides pour montrer à l'utilisateur que sa recherche
    // est en attente de réponse
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

    // Créer la vue qui doit être affichée (movie != null) l'image du film recherché
    // ou (movie == null) la plaquette de chargement
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
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieActivity.setSelection(movie);
                ihm.demarrerActivite(SearchActivity.this, SearchActivity.this, MovieActivity.class);
            }
        });
        return imageView;
    }

    public void addResults(List<Movie> movies, int currentPage, int amount, boolean hasNextPage) {
        if (currentPage == 1) {
            // Nettoyer la liste de résultats et revenir en haut des résultats si 'page' vaut 1
            // car cela signifie que nous effectuons une recherche avec un query différent
            this.results.clear();
            scroll_result.scrollTo(0, 0);
        }
        this.currentPage = currentPage;
        this.hasNextPage = hasNextPage;
        result_amount.setVisibility(View.VISIBLE);
        result_amount.setText(amount == 0 ? "Aucun résultat" : amount + " résultat" + (amount == 1 ? "" : "s"));
        int lastAmount = results.size();
        this.results.addAll(movies);
        //Supprimer le logo de chargement s'il est présent
        if (loadingImage != null) {
            loadingImage.clearAnimation();
            search_result.removeView(loadingImage);
            loadingImage = null;
        }
        updateResultsUI(lastAmount);
        // Incrémenter le token pour signifier que la file d'attente précédente doit s'arrêter
        currentQueueToken++;
        // Charger la nouvelle file
        loadNextInQueue();
        if (hasNextPage) {
            // Ajouter le logo de chargement en bas s'il existe une page suivante
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.loading);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,200, 0));
            imageView.setPadding(0, 50, 0, 50);
            imageView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.loading));
            loadingImage = imageView;
            search_result.addView(imageView);
        }
    }

    // Charger l'image de la plaquette suivante
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
            // Vérifier que le token n'a pas changer
            if (token == currentQueueToken) {
                loadNextInQueue();
            }
        });
    }

    public List<Movie> getResults() {
        return results;
    }
}
