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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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

        whatShow(); // Configuro el drawer para lo que debe mostrar y lo que no por defecto

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
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent i = null;

        if (id == R.id.profile) {
            i = new Intent(this, Profile.class);
        } else if (id == R.id.login) {
            i = new Intent(this, Login.class);
        } else if (id == R.id.logout) {
            Preferences.savePreferenceString(this, "",
                    Preferences.PREFERENCE_USER_LOGIN);
            Preferences.savePreferenceString(this, "",
                    Preferences.PREFERENCE_USER_NICK);
            Preferences.savePreferenceString(this, "",
                    Preferences.PREFERENCE_USER_FULL_NAME);
            finish();
            i = new Intent(this, MainActivity.class);
            Toast.makeText(this,"Sesión cerrada",
                    Toast.LENGTH_SHORT).show();
        } else if (id == R.id.principal) {
//            i = new Intent(this, MainActivity.class);
        } else if (id == R.id.maps) {
//            i = new Intent(this, GoogleMaps.class);
            android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedor, new UserSearch());
            transaction.commit();
        } else if (id == R.id.search_user) {
            i = new Intent(this, UserSearch.class);
        } else if (id == R.id.search_estab) {
            i = new Intent(this, EstablishmentSearch.class);
        }
        if (i != null)
            startActivity(i);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void whatShow() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View viewHeader = navigationView.inflateHeaderView(R.layout.nav_header_main);
        Menu nav_Menu = navigationView.getMenu();

        ImageView imageProfile = (ImageView) viewHeader.findViewById(R.id.imageProfile);
        TextView nickProfile = (TextView) viewHeader.findViewById(R.id.nickProfile);
        TextView fullNameProfile = (TextView) viewHeader.findViewById(R.id.fullNameProfile);

        imageProfile.setVisibility(View.INVISIBLE);
        nickProfile.setVisibility(View.INVISIBLE);
        fullNameProfile.setVisibility(View.INVISIBLE);
        nav_Menu.findItem(R.id.profile).setVisible(false);
        nav_Menu.findItem(R.id.logout).setVisible(false);

        if (Preferences.obtenerPreferenceString(this,
                Preferences.PREFERENCE_USER_LOGIN).length() > 0) {
            nav_Menu.findItem(R.id.profile).setVisible(true);
            nav_Menu.findItem(R.id.logout).setVisible(true);
            nav_Menu.findItem(R.id.login).setVisible(false);
            imageProfile.setVisibility(View.VISIBLE);
            nickProfile.setVisibility(View.VISIBLE);
            nickProfile.setText(Preferences.obtenerPreferenceString(this,
                    Preferences.PREFERENCE_USER_NICK));
            fullNameProfile.setVisibility(View.VISIBLE);
            fullNameProfile.setText(Preferences.obtenerPreferenceString(this,
                    Preferences.PREFERENCE_USER_FULL_NAME));
        }
    }
}
