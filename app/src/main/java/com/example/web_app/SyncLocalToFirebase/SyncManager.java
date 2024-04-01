package com.example.web_app.SyncLocalToFirebase;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.example.web_app.localDb.AppDatabase;
import com.example.web_app.localDb.SignalementEntity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncManager {
    private Context context;
    private AppDatabase appDatabase;
    private FirebaseFirestore firestore;

    public SyncManager(Context context) {
        this.context = context;
        this.appDatabase = AppDatabase.getInstance(context);
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void syncWithFirebase() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            List<SignalementEntity> offlineSignalements = appDatabase.signalementDao().getAll();
            Log.d("DB_DATA", "Signalements récupérés depuis la base de données: " + offlineSignalements);
            Log.d("SYNC_MANAGER", "Synchronisation avec Firebase en cours...");
            for (SignalementEntity signalement : offlineSignalements) {
                final SignalementEntity finalSignalement = signalement;
                long cip = finalSignalement.getCIP();
                String description = finalSignalement.getDescription();
                boolean traite = finalSignalement.isTraite();
                String userId = finalSignalement.getUserId();
                Date date = finalSignalement.getDate();

                Log.d("SYNC_MANAGER", "Signalement à synchroniser avec Firebase - CIP: " + cip + ", Description: " + description  + ", Traitement: " + traite + ", User ID: " + userId);

                firestore.collection("Medicaments")
                        .whereEqualTo("CIP13", cip)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                QuerySnapshot document = task.getResult();
                                if (!document.isEmpty()) {
                                    String denomination = document.getDocuments().get(0).getString("Denomination");

                                    Map<String, Object> signalementMap = new HashMap<>();
                                    signalementMap.put("CIP13", cip);
                                    signalementMap.put("description", description);
                                    signalementMap.put("denomination", denomination);
                                    signalementMap.put("traité", traite);
                                    signalementMap.put("user_id", userId);
                                    signalementMap.put("Date", date);

                                    firestore.collection("Signalements").add(signalementMap)
                                            .addOnSuccessListener(documentReference -> {
                                                Log.d("SYNC_MANAGER", "Signalement synchronisé avec Firebase");
                                                new Thread(() -> appDatabase.signalementDao().delete(finalSignalement)).start();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(context, "Échec de la synchronisation avec Firebase : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    Log.d("SYNC_MANAGER", "Le code CIP spécifié n'existe pas dans la base de données.");
                                }
                            } else {
                                Log.e("SYNC_MANAGER", "Erreur lors de la récupération des données de Firebase : " + task.getException().getMessage());
                                Toast.makeText(context, "Erreur : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }


}

