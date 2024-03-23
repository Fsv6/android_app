package com.example.web_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ConfirmationSignalment extends AppCompatActivity {

    private Button Btn_retour_pprincipale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_signalment);
        Btn_retour_pprincipale = findViewById(R.id.btn_retour_pprincipale);

        Btn_retour_pprincipale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConfirmationSignalment.this, MainActivity.class));
            }
        });
    }
}