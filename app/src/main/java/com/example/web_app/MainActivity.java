package com.example.web_app;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import kotlin.Unit;

import com.bumptech.glide.Glide;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import kotlin.jvm.functions.Function1;
public class MainActivity extends AppCompatActivity {
    protected final int home = 1;
    protected final int liste = 2;
    protected final int profil = 3;
    private TextView TextViewGreeting;
    private FirebaseFirestore fStor;
    private FirebaseAuth auth;
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fStor = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();
        TextViewGreeting = findViewById(R.id.textViewGreeting);
        loadUserData();
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
    private void loadUserData() {
        DocumentReference documentReference = fStor.collection("users").document(userID);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String userName = document.getString("Nom");
                        String userSName = document.getString("Prenom");
                        if (userSName != null) {
                            TextViewGreeting.setText("Bonjour " + userName +" "+ userSName + " !");
                        }
                        else {
                            TextViewGreeting.setText("Bonjour " + userName + " !");
                        }


                    }
                }
            }
        });
    }
}