package com.example.web_app;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
/*import com.google.android.gms.auth.api.signin.GoogleSignInClient;*/
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class Connexion extends AppCompatActivity {

    private EditText loginEmail, loginPassword;
    private TextView  forgotpassword;
    private Button loginButton, Signup_button;
    private FirebaseAuth auth;
    String userID;
    private CardView BtnSignInWithGoogleCardView;
    private FirebaseFirestore fStore;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        Signup_button = findViewById(R.id.signup_button);
        forgotpassword = findViewById(R.id.forgot_password);
        BtnSignInWithGoogleCardView = findViewById(R.id.btnSignInWithGoogle);

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        if (auth.getCurrentUser() != null) {
            redirectToMainActivity();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);




        BtnSignInWithGoogleCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignin();
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = loginEmail.getText().toString();
                String pass = loginPassword.getText().toString();

                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!pass.isEmpty()) {
                        if (email.equals("admin@admin.com") && pass.equals("admin2024")) {
                            Toast.makeText(Connexion.this, "Connexion réussie en tant qu'administrateur", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Connexion.this, Admin.class));
                            finish();
                        } else {
                            auth.signInWithEmailAndPassword(email, pass)
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            Toast.makeText(Connexion.this, "Connexion réussie", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(Connexion.this, MainActivity.class));
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Connexion.this, "Échec de la connexion", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        loginPassword.setError("Champs vides non autorisés");
                    }
                } else if (email.isEmpty()) {
                    loginEmail.setError("Champs vides non autorisés");
                } else {
                    loginEmail.setError("Veuillez saisir l'e-mail correct");
                }

            }
        });

        Signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Connexion.this, InscriptionActivity.class));
            }
        });
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Connexion.this, ForgetPassword.class));
            }
        });




    }

    private void googleSignin() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(Connexion.this, "Google sign in failed", Toast.LENGTH_SHORT).show();

                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                addUserToFirestore(user);
                            }
                            redirectToMainActivity();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Connexion.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(Connexion.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void addUserToFirestore(FirebaseUser user) {
        fStore = FirebaseFirestore.getInstance();

        DocumentReference userRef = fStore.collection("users").document(user.getUid());
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "User déjà dans Firestore");
                    } else {
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("Email", user.getEmail());

                        String[] nameParts = user.getDisplayName().split(" ");
                        if (nameParts.length > 0) {
                            userData.put("Nom", nameParts[0]);
                        }

                        fStore.collection("users").document(user.getUid())
                                .set(userData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "User added to Firestore successfully");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding user to Firestore", e);
                                    }
                                });
                    }
                } else {
                    Log.d(TAG, "Failed to check user existence in Firestore", task.getException());
                }
            }
        });
    }
}





