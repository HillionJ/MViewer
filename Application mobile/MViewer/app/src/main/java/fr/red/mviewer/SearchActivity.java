package fr.red.mviewer;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import fr.red.mviewer.tmdb.TheMovieDB;
import fr.red.mviewer.utils.IHM;
import fr.red.mviewer.utils.Movie;
import fr.red.mviewer.widgets.ResultWidget;

public class SearchActivity extends AppCompatActivity {

    private ResultWidget resultWidget;

    private TextView result_amount;
    private IHM ihm = IHM.getIHM();
    private TheMovieDB theMovieDB = TheMovieDB.getInstance();
    private LinearLayout search_result;
    private ScrollView scroll_result;
    private SearchView searchView;

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

        SearchActivity lastActivity = (SearchActivity) ihm.getActivite(SearchActivity.class);
        ihm.ajouterIHM(this);
        ihm.applyDarkTheme();

        searchView = findViewById(R.id.searchView);
        searchView.setIconified(false);

        search_result = findViewById(R.id.search_result);
        result_amount = findViewById(R.id.result_amount);
        result_amount.setVisibility(View.INVISIBLE);
        scroll_result = findViewById(R.id.scroll_result);

        if (lastActivity == null) {
            this.resultWidget = new ResultWidget(search_result, this);
        } else {
            this.resultWidget = new ResultWidget(search_result, this, lastActivity.resultWidget);
        }
        loopCheckScroll();

        manageQuery();

        resultWidget.init();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ihm.ajouterIHM(this);
    }

    private void manageQuery() {
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
                        if (this.scheduler == newScheduler) {
                            updateQuery(newText);
                        }
                    });
                }, 500, TimeUnit.MILLISECONDS);
                return false;
            }
        });
    }

    private void updateQuery(String newText) {
        if (newText.isEmpty()) {
            result_amount.setVisibility(View.INVISIBLE);
            clearResults();
        } else {
            if (search_result.getChildCount() == 0) {
                resultWidget.displayLoadingResultsUI();
            }
            theMovieDB.search(newText);
        }
    }

    public void addResults(List<Movie> movies, int currentPage, int amount, boolean hasNextPage) {
        if (currentPage == 1) {
            scroll_result.scrollTo(0, 0);
        }
        setResultText(amount == 0 ? "Aucun résultat" : amount + " résultat" + (amount == 1 ? "" : "s"));
        resultWidget.addResults(movies, currentPage, hasNextPage);
    }

    public void setResultText(String text) {
        result_amount.setText(text);
        result_amount.setVisibility(View.VISIBLE);
    }

    //Vérifier constament si on arrive au bout de la liste des résultats pour afficher les suivantes
    public void loopCheckScroll() {
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
            //Vérifier si on arrive au bout du scroll
            if (scroll_result.getScrollY() == scroll_result.getChildAt(0).getHeight() - scroll_result.getHeight() && resultWidget.hasNextPage()) {
                resultWidget.setHasNextPage(false);
                resultWidget.setCurrentPage(resultWidget.getCurrentPage() + 1);
                theMovieDB.search(searchView.getQuery().toString(), resultWidget.getCurrentPage());
            }
        },0, 1, TimeUnit.SECONDS);
    }

    public void clearResults() {
        resultWidget.removeResultsUI();
        resultWidget.setHasNextPage(false);
    }
}
