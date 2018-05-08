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
import android.view.MenuItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import domain.Restaurante;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycle)
    RecyclerView recycle;

    private RestaurantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        configToobar();
        configAdapter();
        configReclyclerView();

        generateRestaurant();

        // Todoo esto es para el desplegable
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        // Para modificar el include de cada drawer
//        String strIncludeId = "include";
//        int includeId = getResources().getIdentifier(strIncludeId,"id", getPackageName());
//        CoordinatorLayout texto = (CoordinatorLayout) drawer.findViewById(includeId);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

    // Menú de los 3 puntitos
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    // Deplegable de los 3 puntitos
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        // Esto son los 3 puntitos desplegables del menú
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    @NonNull
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent i = null;

        if (id == R.id.profile) {
            i = new Intent(MainActivity.this, Profile.class);
        } else if (id == R.id.logout) {
            // TODO
        } else if (id == R.id.principal) {
            i = new Intent(MainActivity.this, MainActivity.class);
        } else if (id == R.id.login) {
            i = new Intent(MainActivity.this, Login.class);
        } else if (id == R.id.maps) {
            i = new Intent(MainActivity.this, GoogleMaps.class);
        } else if (id == R.id.search_user) {
            i = new Intent(MainActivity.this, UserSearch.class);
        } else if (id == R.id.search_estab) {
            i = new Intent(MainActivity.this, EstablishmentSearch.class);
        }
        if (i != null)
            startActivity(i);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Métodos auxiliares --------------------------------------------------------------------------

    @Override
    public void onItemClick(Restaurante restaurante) {

    }

    @Override
    public void onLongItemClick(Restaurante restaurante) {

    }

    private void configToobar() {
        setSupportActionBar(toolbar);
    }

    private void configAdapter() {
        adapter = new RestaurantAdapter(new ArrayList<Restaurante>(), this);
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
            Restaurante restaurante = new Restaurante(nombres[i], direcciones[i], latitudes[i],
                    longitudes[i], cierres[i], aperturas[i], telefonos[i], notasMedia[i]);
            adapter.add(restaurante);
        }
    }

    // Botones -------------------------------------------------------------------------------------

    @OnClick(R.id.maps)
    public void goToMaps() {
        Intent i = new Intent(MainActivity.this, GoogleMaps.class);
        startActivity(i);
    }

    @OnClick(R.id.fab)
    public void goToMapsFloat() {
        Intent i = new Intent(MainActivity.this, GoogleMaps.class);
        startActivity(i);
    }

    @OnClick(R.id.iniciarSesion)
    public void goToLogin() {
        Intent i = new Intent(MainActivity.this, Login.class);
        startActivity(i);
    }
}
