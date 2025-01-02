package fr.red.mviewer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fr.red.mviewer.utils.IHM;

public class Login extends AppCompatActivity {
    private EditText login, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialiser l'IHM si elle n'est pas déjà créée
        IHM ihm = IHM.getIHM() == null ? new IHM(this) : IHM.getIHM();
        ihm.ajouterIHM(this);
        ihm.applyDarkTheme();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent i = new Intent(Login.this, FlowActivity.class);
            startActivity(i);
        } else {
            login = findViewById(R.id.login);
            password = findViewById(R.id.password);
        }

    }

    // Méthode appelée lors du clic sur le bouton "Se connecter"
    public void submit(View v){
        try {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            // Connecte l'utilisateur à son compte
            auth.signInWithEmailAndPassword(login.getText().toString(), password.getText().toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent i = new Intent(Login.this, FlowActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Veuillez rentrer des identifiants valides.", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Veuillez rentrer des identifiants valides.", Toast.LENGTH_LONG).show();
        }
    }

    // Méthode appelée lors du clic sur le bouton "Créer un compte"
    public void move_to_register(View v){
        Intent i = new Intent(Login.this, Register.class);
        startActivity(i);
    }
}