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

public class Register extends AppCompatActivity {
    EditText login, password1, password2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        login = findViewById(R.id.login);
        password1 = findViewById(R.id.password1);
        password2 = findViewById(R.id.password2);
    }

    public void register(View v){
        if (password1.getText().toString().equals(password2.getText().toString())) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(login.getText().toString(), password1.getText().toString());
            Intent i = new Intent(Register.this, FlowActivity.class);
            startActivity(i);
        } else {
            Toast.makeText(getApplicationContext(), "Les 2 mots de passe doivent correspondre", Toast.LENGTH_LONG).show();
        }
    }

}