package fr.red.mviewer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Rename extends AppCompatActivity {
    private EditText new_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rename);

        new_name = findViewById(R.id.new_name);
    }

    // Méthode appelée lors du clic sur le bouton de validation dans l'activité de changement de nom
    public void rename(View v){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        UserProfileChangeRequest new_profile = new UserProfileChangeRequest.Builder().setDisplayName(new_name.getText().toString()).build(); // Instancie le changement de nom

        // Applique les changements et démarre la page principale quand l'update est terminée
        auth.getCurrentUser().updateProfile(new_profile).addOnCompleteListener(task -> {
            Intent i = new Intent(Rename.this, FlowActivity.class);
            startActivity(i);
            finish();
        });


    }

}