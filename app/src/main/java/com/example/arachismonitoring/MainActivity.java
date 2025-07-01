package com.example.arachismonitoring;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.android.gms.common.api.ApiException;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;
import android.view.View;

import java.util.Collections;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements ControllerMenuFragment.OnControllerMenuListener, EntryFragment.OnEntryListener {
    private static final int RC_SIGN_IN = 1000;
    private GoogleSignInClient mGoogleSignInClient;
    private String selectedRole;
    private GoogleSignInAccount signedInAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(SheetsScopes.SPREADSHEETS))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Show EntryFragment on launch
        if (savedInstanceState == null) {
            EntryFragment entryFragment = new EntryFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, entryFragment)
                    .commit();
        }
    }

    @Override
    public void onCourierClicked() {
        selectedRole = "Courier";
        signIn();
    }

    @Override
    public void onControllerClicked() {
        selectedRole = "Controller";
        signIn();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                signedInAccount = task.getResult(ApiException.class);
                Toast.makeText(this, "Signed in as " + selectedRole + ": " + signedInAccount.getEmail(), Toast.LENGTH_SHORT).show();
                if ("Controller".equals(selectedRole)) {
                    showControllerMenuFragment();
                } else if ("Courier".equals(selectedRole)) {
                    showCourierFragment();
                } else {
                    showDataFragment();
                }
            } catch (ApiException e) {
                Log.e("SIGN_IN", "Sign-in failed", e);
                Toast.makeText(this, "Sign-in failed: " + e.getStatusCode(), Toast.LENGTH_LONG).show();
                // Handle sign-in failure
                if (e.getStatusCode() == 10) {
                    // Developer error - check your configuration
                    Toast.makeText(this, "Please check your Google Sign-In configuration", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void showControllerMenuFragment() {
        ControllerMenuFragment fragment = new ControllerMenuFragment();
        Bundle args = new Bundle();
        args.putParcelable("account", signedInAccount);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("controller_menu")
                .commit();
    }

    private void showCourierFragment() {
        CourierFragment fragment = new CourierFragment();
        Bundle args = new Bundle();
        args.putParcelable("account", signedInAccount);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("courier")
                .commit();
    }

    private void showDataFragment() {
        DataFragment fragment = new DataFragment();
        Bundle args = new Bundle();
        args.putParcelable("account", signedInAccount);
        args.putString("role", selectedRole);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("data")
                .commit();
    }

    private void showControllerDeliveryFragment() {
        ControllerDeliveryFragment fragment = new ControllerDeliveryFragment();
        Bundle args = new Bundle();
        args.putParcelable("account", signedInAccount);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("controller_delivery")
                .commit();
    }

    @Override
    public void onDeliverySelected(GoogleSignInAccount account) {
        showControllerDeliveryFragment();
    }

    @Override
    public void onDataSelected(GoogleSignInAccount account) {
        showDataFragment();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onOrderStatusHistorySelected(GoogleSignInAccount account) {
        OrderStatusHistoryFragment fragment = new OrderStatusHistoryFragment();
        Bundle args = new Bundle();
        args.putParcelable("account", account);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack("order_status_history")
            .commit();
    }
}

