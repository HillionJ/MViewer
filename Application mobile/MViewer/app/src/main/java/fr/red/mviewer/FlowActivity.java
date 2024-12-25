package fr.red.mviewer;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Rect;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.Executors;

import fr.red.mviewer.utils.IHM;
import fr.red.mviewer.widgets.FlowWidget;

public class FlowActivity extends AppCompatActivity {

    private FlowWidget flowWidget = null;

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

        IHM ihm = IHM.getIHM() == null ? new IHM(this) : IHM.getIHM();
        ihm.ajouterIHM(this);
        ihm.applyDarkTheme();

        flowWidget = new FlowWidget(findViewById(R.id.idFlow), findViewById(R.id.scrollView));

        SearchView searchView = findViewById(R.id.searchView3);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IHM.getIHM().demarrerActivite(FlowActivity.this, FlowActivity.this, SearchActivity.class);
            }
        });
    }

    public void updateFlow() {
        Log.d("_RED", "updateFlow()");
        flowWidget.updateFlow();
    }

    public void scrollToNext() {
        flowWidget.scrollToNext();
    }

    // Méthode pour scroller automatiquement vers la plaquette précédente
    public void scrollToPrevious() {
        flowWidget.scrollToPrevious();
    }
}