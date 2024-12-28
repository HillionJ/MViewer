package fr.red.mviewer.authentification;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fr.red.mviewer.MainActivity;
import fr.red.mviewer.R;

public class Login extends AppCompatActivity {
    EditText login, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
    }

    public void submit(View v){
        try {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signInWithEmailAndPassword(login.getText().toString(), password.getText().toString());
            FirebaseUser user = auth.getCurrentUser();
            Intent i = new Intent(Login.this, MainActivity.class);
            i.putExtra("msg", user.getEmail());
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Veuillez rentrer des identifiants valides.", Toast.LENGTH_LONG).show();
        }
    }

    public void move_to_register(View v){
        Intent i = new Intent(Login.this, Register.class);
        startActivity(i);
    }
}