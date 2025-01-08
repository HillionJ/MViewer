package fr.red.mviewer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executors;

import fr.red.mviewer.tmdb.TheMovieDB;
import fr.red.mviewer.utils.IHM;
import fr.red.mviewer.widgets.FlowWidget;

public class FlowActivity extends AppCompatActivity {

    private FlowWidget flowWidget = null;
    private IHM ihm;

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_flow);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ihm = IHM.getIHM();
        ihm.ajouterIHM(this);
        ihm.applyDarkTheme();

        flowWidget = new FlowWidget(findViewById(R.id.idFlow), findViewById(R.id.scrollView), this);
        // Définir un titre personnalisé pour l'ActionBar
        if (getSupportActionBar() != null) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser().getDisplayName() == null) {
                String name = auth.getCurrentUser().getEmail().toString();
                getSupportActionBar().setTitle(name);
            } else {
                getSupportActionBar().setTitle(auth.getCurrentUser().getDisplayName());
            }

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ihm.ajouterIHM(this);

        if (TheMovieDB.getInstance().isErrored() && !flowWidget.isPendingReload()) {
            flowWidget.updateFlow();
        }
    }

    // Création du menu lors du chargement de l'activité
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    // Gestion des choix du menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId()== R.id.logout){
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut(); // Déconnecte l'utilisateur
            Intent i = new Intent(FlowActivity.this, Login.class);
            startActivity(i);
            finish();
            return true;
        } else if (item.getItemId()== R.id.search){
            IHM.getIHM().demarrerActivite(FlowActivity.this, FlowActivity.this, SearchActivity.class); // Lance l'activité de recherche d'un film
            return true;
        } else if (item.getItemId()== R.id.rename){
            Intent i = new Intent(FlowActivity.this, Rename.class); // Lance l'activité pour renommer l'utilisateur
            startActivity(i);
            return true;
        }
        return true;
    }

    public void updateFlow() {
        flowWidget.updateFlow();
    }

    public void scrollToNext() {
        flowWidget.scrollToNext();
    }

    public void scrollToPrevious() {
        flowWidget.scrollToPrevious();
    }
}