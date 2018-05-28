package com.my.cristian.guiamiguelin;

import android.app.Fragment;
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
            toolbar.setTitle("Guía Miguelín");
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
        android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment fragment = null;
        boolean entra = false;

        for(int e=0; e < getFragmentManager().getBackStackEntryCount(); e++)
            getFragmentManager().popBackStack();

        if (id == R.id.profile) {
            toolbar.setTitle("Perfil de usuario");
            fragment = new Profile();
        } else if (id == R.id.login) {
            toolbar.setTitle("Iniciar sesión");
            fragment = new Login();
        } else if (id == R.id.logout) {
            Preferences.savePreferenceString(this, "",
                    Preferences.PREFERENCE_USER_LOGIN);
            Preferences.savePreferenceString(this, "",
                    Preferences.PREFERENCE_USER_NICK);
            Preferences.savePreferenceString(this, "",
                    Preferences.PREFERENCE_USER_FULL_NAME);
            toolbar.setTitle("Guía Miguelín");
            fragment = new ContentMain();
            Toast.makeText(this,"Sesión cerrada",
                    Toast.LENGTH_LONG).show();
        } else if (id == R.id.principal) {
            toolbar.setTitle("Guía Miguelín");
            fragment = new ContentMain();
            entra = true;
        } else if (id == R.id.maps) {
            i = new Intent(this, GoogleMaps.class);
        } else if (id == R.id.search_user) {
            toolbar.setTitle("Búscaqueda de usuarios");
            fragment = new UserSearch();
            // Hago que el item se seleccione
            NavigationView navigationView = findViewById(R.id.nav_view);
            MenuItem itemSearch = navigationView.getMenu().findItem(R.id.search_user);
            itemSearch.setCheckable(true);
            itemSearch.setChecked(true);
        } else if (id == R.id.search_estab) {
            toolbar.setTitle("Búscaqueda de establecimientos");
            fragment = new EstablishmentSearch();
            // Hago que el item se seleccione
            NavigationView navigationView = findViewById(R.id.nav_view);
            MenuItem itemSearch = navigationView.getMenu().findItem(R.id.search_estab);
            itemSearch.setCheckable(true);
            itemSearch.setChecked(true);
        }
        if (i != null)
            startActivity(i);
        if(fragment != null){
            transaction.replace(R.id.contenedor, fragment).addToBackStack(null).commit();
            if(entra)
                getFragmentManager().popBackStack(); // Esto es para que la vista contentMain no se repita
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
