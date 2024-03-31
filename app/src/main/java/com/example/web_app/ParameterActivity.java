package com.example.web_app;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

public class ParameterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.preferences_container, new ParameterFragment());
        ft.commit();

        // Gérer le clic sur le bouton de déconnexion
        Button logoutButton = findViewById(R.id.btn_deco);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Déconnexion
                FirebaseAuth.getInstance().signOut();
                // Redirection vers l'écran de connexion
                Intent intent = new Intent(ParameterActivity.this, Connexion.class);
                startActivity(intent);
                finish(); // Optionnel : fermer cette activité après la déconnexion
            }
        });
    }

}
