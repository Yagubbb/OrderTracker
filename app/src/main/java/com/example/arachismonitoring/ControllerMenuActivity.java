package com.example.arachismonitoring;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class ControllerMenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller_menu);

        Button btnMainPage = findViewById(R.id.btnMainPage);
        Button btnDelivery = findViewById(R.id.btnDelivery);
        Button btnData = findViewById(R.id.btnData);

        GoogleSignInAccount account = getIntent().getParcelableExtra("account");

        btnMainPage.setOnClickListener(v -> {
            // Placeholder: Show a toast or implement main page logic
            // Toast.makeText(this, "Main page pressed", Toast.LENGTH_SHORT).show();
        });

        btnDelivery.setOnClickListener(v -> {
            Intent intent = new Intent(this, DeliveryActivity.class);
            intent.putExtra("account", account);
            startActivity(intent);
        });

        btnData.setOnClickListener(v -> {
            // Placeholder: Show a toast or implement data logic
            // Toast.makeText(this, "Data pressed", Toast.LENGTH_SHORT).show();
        });
    }
} 