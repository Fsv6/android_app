package com.example.web_app;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import kotlin.Unit;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import kotlin.jvm.functions.Function1;
public class MainActivity extends AppCompatActivity {
    protected final int home = 1;
    protected final int liste = 2;
    protected final int profil = 3;
    Button Reservercreneu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Reservercreneu = findViewById(R.id.reservercreneu);
        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottomnav);
        bottomNavigation.add(new MeowBottomNavigation.Model(profil, R.drawable.baseline_person_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(home, R.drawable.baseline_home_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(liste, R.drawable.baseline_list_24));
        bottomNavigation.show(1, true);
        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                /* Toast.makeText(MainActivity.this,"Iteam click"+model.getId(),Toast.LENGTH_SHORT).show();*/
                return null;
            }
        });



        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                Intent intent;
                switch (model.getId()) {
                    case home:
                        // Créer un Intent pour l'activité Home
                        intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case liste:
                        // Créer un Intent pour l'activité Dashboard
                        intent = new Intent(MainActivity.this, ConfirmationSignalment.class);
                        startActivity(intent);
                        break;
                    case profil:
                        // Créer un Intent pour l'activité Profil
                        intent = new Intent(MainActivity.this, Profile.class);
                        startActivity(intent);
                        break;
                }
                return null;
            }
        });
    }
}