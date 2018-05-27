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
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Esto es el desplegable
        configToobar();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Para iniciar la vista principal contentMain
        android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.contenedor, new ContentMain());
        transaction.commit();
    }

    // Esto es para cuando se pulse en navigation drawer (para desplegarlo)
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Para nombrar el action bar
    private void configToobar() {
        setSupportActionBar(toolbar);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    @NonNull
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent i = null;

        if (id == R.id.profile) {
            toolbar.setTitle("Perfil de usuario");
            android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedor, new Profile());
            transaction.commit();
        } else if (id == R.id.login) {
            toolbar.setTitle("Iniciar sesión");
            android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedor, new Login());
            transaction.commit();
        } else if (id == R.id.logout) {
            Preferences.savePreferenceString(this, "",
                    Preferences.PREFERENCE_USER_LOGIN);
            Preferences.savePreferenceString(this, "",
                    Preferences.PREFERENCE_USER_NICK);
            Preferences.savePreferenceString(this, "",
                    Preferences.PREFERENCE_USER_FULL_NAME);
            toolbar.setTitle("Guía Miguelín");
            android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedor, new ContentMain());
            transaction.commit();
            Toast.makeText(this,"Sesión cerrada",
                    Toast.LENGTH_LONG).show();
        } else if (id == R.id.principal) {
            toolbar.setTitle("Guía Miguelín");
            android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedor, new ContentMain());
            transaction.commit();
        } else if (id == R.id.maps) {
            i = new Intent(this, GoogleMaps.class);
        } else if (id == R.id.search_user) {
            toolbar.setTitle("Búscaqueda de usuarios");
            android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedor, new UserSearch());
            transaction.commit();
        } else if (id == R.id.search_estab) {
            toolbar.setTitle("Búscaqueda de establecimientos");
            android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedor, new EstablishmentSearch());
            transaction.commit();
        }
        if (i != null)
            startActivity(i);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
