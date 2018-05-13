package com.my.cristian.guiamiguelin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Login extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar_login)
    Toolbar toolbar;
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.passaword)
    EditText passaword;
    @BindView(R.id.log_in)
    Button logIn;
    @BindView(R.id.textRegister)
    TextView textRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // Esto es el desplegable
        DrawerLayout drawer = findViewById(R.id.drawer_layout_login);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_login);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    @NonNull
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent i = null;

        if (id == R.id.profile) {
            i = new Intent(Login.this, Profile.class);
        } else if (id == R.id.logout) {
            // TODO
        } else if (id == R.id.principal) {
            i = new Intent(Login.this, MainActivity.class);
        } else if (id == R.id.login) {
            i = new Intent(Login.this, Login.class);
        } else if (id == R.id.maps) {
            i = new Intent(Login.this, GoogleMaps.class);
        } else if (id == R.id.search_user) {
            i = new Intent(Login.this, UserSearch.class);
        } else if (id == R.id.search_estab) {
            i = new Intent(Login.this, EstablishmentSearch.class);
        }
        if (i != null)
            startActivity(i);

        DrawerLayout drawer = findViewById(R.id.drawer_layout_login);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Botones -------------------------------------------------------------------------------------

    @OnClick(R.id.log_in)
    public void loginOnClick() {
        new MongoDB().mongoAPI("/status", "POST", this); // Prueba para el servidor heroku
        Intent i = new Intent(Login.this, MainActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.textRegister)
    public void onViewClicked() {
        Intent i = new Intent(Login.this, Register.class);
        startActivity(i);
    }
}
