package fr.red.mviewer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.util.List;

import fr.red.mviewer.tmdb.TheMovieDB;
import fr.red.mviewer.utils.IHM;
import fr.red.mviewer.utils.Movie;
import fr.red.mviewer.utils.MovieGenre;
import fr.red.mviewer.widgets.ResultWidget;

public class MovieActivity extends AppCompatActivity {

    private static Movie selection = null;

    public static void setSelection(Movie movie) {
        selection = movie;
        newSelection = true;
    }
    private static boolean newSelection = false;

    private ImageView image;
    private TextView title;
    private TextView description;
    private LinearLayout genres;
    private LinearLayout announce_btn;
    private TextView description_next;
    private IHM ihm;
    private ResultWidget resultWidget;
    private LinearLayout simillar_results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (selection == null) {
            ihm.fermerActivite(MovieActivity.class);
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.ihm = IHM.getIHM();
        MovieActivity lastActivity = (MovieActivity) ihm.getActivite(MovieActivity.class);
        this.ihm.ajouterIHM(this);
        this.ihm.applyDarkTheme();

        image = findViewById(R.id.info_image);
        title = findViewById(R.id.info_title);
        description = findViewById(R.id.info_description);
        description_next = findViewById(R.id.info_description_next);
        genres = findViewById(R.id.info_genres);
        announce_btn = findViewById(R.id.announce_btn);
        simillar_results = findViewById(R.id.simillar_results);

        if (lastActivity == null) {
            this.resultWidget = new ResultWidget(simillar_results, this);
        } else {
            this.resultWidget = new ResultWidget(simillar_results, this, lastActivity.resultWidget);
        }
        if (newSelection) {
            resultWidget.setOnInit(() -> {
                resultWidget.displayLoadingResultsUI();
                TheMovieDB.getInstance().fetchSimillar(selection);
            });
        }
        newSelection = false;

        announce_btn.setOnClickListener(v -> {
            openYouTubeSearch(selection.getTitle() + " bande annonce");
        });
        title.setText(selection.getTitle());
        description.setText(selection.getOverview());
        description.post(this::setDescription);
        displayBanner();
        setGenres();

        resultWidget.init();
    }

    // Mettre en place ou non un déroulement du texte de la descrition si elle fait plus de 2 lignes
    private void setDescription() {
        if (description.getLineCount() > 2) {
            description.setMaxLines(2);
            description.setEllipsize(TextUtils.TruncateAt.END);
            description_next.setVisibility(View.VISIBLE);

            description.setOnClickListener(new View.OnClickListener() {
                private boolean isExpanded = false;

                @Override
                public void onClick(View v) {
                    if (isExpanded) {
                        description.setMaxLines(2);
                        description.setEllipsize(TextUtils.TruncateAt.END);
                        description_next.setVisibility(View.VISIBLE);
                    } else {
                        description.setMaxLines(Integer.MAX_VALUE);
                        description.setEllipsize(null);
                        description_next.setVisibility(View.INVISIBLE);
                    }
                    isExpanded = !isExpanded;
                }
            });
        }
    }

    // Afficher l'image réduite (backdrop_path)
    private void displayBanner() {
        Glide.with(ihm.getActiviteActive())
                .load("https://image.tmdb.org/t/p/w500" + selection.getBackdrop_path())
                .placeholder(R.drawable.gray_background)
                .error(R.drawable.gray_background)
                .into(image);
    }

    // Afficher les genres
    private void setGenres() {
        for (MovieGenre genre : selection.getGenre_ids()) {
            TextView textView = new TextView(this);
            textView.setText(genre.getName());
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            textView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
            textView.setTextColor(getResources().getColor(R.color.white));
            genres.addView(textView);
        }
    }

    // Ouvrir l'application youtube avec la recherche 'query'
    public void openYouTubeSearch(String query) {
        Intent intent = new Intent(Intent.ACTION_SEARCH);
        intent.setPackage("com.google.android.youtube");
        intent.putExtra("query", query);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void addResults(List<Movie> movies) {
        resultWidget.addResults(movies, 1, false);
    }
}


