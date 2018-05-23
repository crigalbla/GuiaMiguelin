package com.my.cristian.guiamiguelin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import domain.Establishment;
import domain.Restaurant;
import domain.TypeRestaurant;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycle)
    RecyclerView recycle;

    private EstablishmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        configAdapter();
        configReclyclerView();
        generateRestaurant();

        // Esto es el desplegable
        configToobar();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        whatShow();
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
//        } else if (id == R.id.principal) {
//            i = new Intent(this, MainActivity.class);
        } else if (id == R.id.maps) {
            i = new Intent(this, GoogleMaps.class);
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

    // Métodos auxiliares --------------------------------------------------------------------------

    @Override
    public void onItemClick(Establishment establishment) {
        Toast.makeText(this,"Acabas de pulsar en un establecimiento",
                Toast.LENGTH_LONG).show();
    }

    private void configAdapter() {
        adapter = new EstablishmentAdapter(new ArrayList<Establishment>(), this);
    }

    private void configReclyclerView() {
        recycle.setLayoutManager(new LinearLayoutManager(this));
        recycle.setAdapter(adapter);
    }

    private void generateRestaurant() {
        // TODO meter aquí todos los establecimientos de la base de datos
        String[] nombres = {"bar", "restaurante", "bar2", "restaurante2"};
        String[] direcciones = {"dirección1", "dirección2", "dirección3", "direcciónDireción"};
        double[] latitudes = {1.0, 1.0, 1.0, 1.0};
        double[] longitudes = {1.0, 1.0, 1.0, 1.0};
        String[] cierres = {"22:00", "22:01", "23:00", "00:00"};
        String[] aperturas = {"22:00", "22:01", "23:00", "00:00"};
        int[] telefonos = {789456123, 987654321, 369258147, 147258369};
        double[] notasMedia = {5.0, 4.7, 2.3, 4.8};

        for (int i = 0; i < 4; i++) {
            Restaurant restaurante = new Restaurant("", nombres[i], direcciones[i],
                    "descripción", latitudes[i], longitudes[i], cierres[i], aperturas[i],
                    telefonos[i], notasMedia[i], new ArrayList<String>(), null,
                    TypeRestaurant.Buffet);
            adapter.add(restaurante);
        }
    }

    private void whatShow(){
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
        nav_Menu.findItem(R.id.principal).setVisible(false);

        if(Preferences.obtenerPreferenceString(this, Preferences.PREFERENCE_USER_LOGIN).length()
                > 0) {
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

    // Botones -------------------------------------------------------------------------------------

    @OnClick(R.id.fab)
    public void goToMapsFloat() {
        Intent i = new Intent(this, GoogleMaps.class);
        startActivity(i);
    }
}
