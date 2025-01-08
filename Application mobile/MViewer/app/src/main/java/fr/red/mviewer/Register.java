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

import fr.red.mviewer.utils.IHM;

public class Register extends AppCompatActivity {
    private EditText login, password1, password2;
    private IHM ihm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ihm = IHM.getIHM();
        ihm.ajouterIHM(this);
        ihm.applyDarkTheme();

        login = findViewById(R.id.login);
        password1 = findViewById(R.id.password1);
        password2 = findViewById(R.id.password2);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ihm.ajouterIHM(this);
    }

    // Méthode appelée lors du clic sur le bouton de validation dans l'activité de création de compte
    public void register(View v){
        if (!login.getText().toString().isEmpty() && !password1.getText().toString().isEmpty()) { // check if both password fields are given
            if (password1.getText().toString().equals(password2.getText().toString())) { // check if the password field and the validation password field are the same
                FirebaseAuth auth = FirebaseAuth.getInstance();
                // Crée un nouvau compte et redirige l'utilisateur vers la page principale
                auth.createUserWithEmailAndPassword(login.getText().toString(), password1.getText().toString()).addOnCompleteListener(task -> {
                    Intent i = new Intent(Register.this, FlowActivity.class);
                    startActivity(i);
                });
            } else {
                Toast.makeText(getApplicationContext(), "Les 2 mots de passe doivent correspondre", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show();
        }
    }

}