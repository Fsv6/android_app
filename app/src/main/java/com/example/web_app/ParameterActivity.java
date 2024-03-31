package com.example.web_app;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.firebase.auth.FirebaseAuth;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ParameterActivity extends AppCompatActivity {
    protected final int home = 1;
    protected final int param = 2;
    protected final int profil = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.preferences_container, new ParameterFragment());
        ft.commit();

        Button logoutButton = findViewById(R.id.btn_deco);
        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottomnav);
        bottomNavigation.add(new MeowBottomNavigation.Model(profil, R.drawable.baseline_person_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(home, R.drawable.baseline_home_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(param, R.drawable.ic_parametres));
        bottomNavigation.show(2, true);
        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                return null;
            }
        });


        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                Intent intent;
                switch (model.getId()) {
                    case home:
                        intent = new Intent(ParameterActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case param:
                        intent = new Intent(ParameterActivity.this, ParameterActivity.class);
                        startActivity(intent);
                        break;
                    case profil:
                        intent = new Intent(ParameterActivity.this, Profile.class);
                        startActivity(intent);
                        break;
                }
                return null;
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ParameterActivity.this, Connexion.class);
                startActivity(intent);
                finish(); }
        });
    }

}
