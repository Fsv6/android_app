package com.example.web_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class ForgetPassword extends AppCompatActivity {
    private ImageView retourlogin;
    private FirebaseAuth auth;
    private EditText forgotPasswordEmail;
    private Button btnForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        /*retourlogin = findViewById(R.id.retour_login);*/
        forgotPasswordEmail = findViewById(R.id.ForgotPasswordEmail);
        btnForgotPassword = findViewById(R.id.btnForgotPasswordSubmit);

        auth = FirebaseAuth.getInstance();

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });














      /*  retourlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgetPassword.this, LoginActivity.class));
            }
        });*/

    }

    private void resetPassword() {
        String email = forgotPasswordEmail.getText().toString().trim();

        if (email.isEmpty()) {
            forgotPasswordEmail.setError("L'email est nécessaire ? ");
            forgotPasswordEmail.findFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            forgotPasswordEmail.setError("Adresse e-mail invalide");
            forgotPasswordEmail.findFocus();
        }
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgetPassword.this, "Consultez votre courrier", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ForgetPassword.this, "Esseyer encore il y'a eu une erreur", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
