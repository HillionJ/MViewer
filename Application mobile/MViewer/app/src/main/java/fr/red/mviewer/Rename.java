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

    public void rename(View v){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        UserProfileChangeRequest new_profile = new UserProfileChangeRequest.Builder().setDisplayName(new_name.getText().toString()).build();

        auth.getCurrentUser().updateProfile(new_profile).addOnCompleteListener(task -> {
            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
            Intent i = new Intent(Rename.this, FlowActivity.class);
            startActivity(i);
            finish();
        });


    }

}