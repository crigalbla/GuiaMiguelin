package com.my.cristian.guiamiguelin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import domain.User;

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
    @BindView(R.id.drawer_layout_login)
    DrawerLayout drawerLayoutLogin;

    private static final Gson gson = new Gson();
    private User userLogged = null;

    private String NICK = "";
    private String PASSWORD = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // Si he iniciado sesión, que me envie directamente a la página principal.
        if(Preferences.obtenerPreferenceString(this,Preferences.PREFERENCE_USER_LOGIN).length() > 0){
            Intent i = new Intent(Login.this, MainActivity.class);
            startActivity(i);
        }

        // Esto es el desplegable
        configToobar();
        DrawerLayout drawer = findViewById(R.id.drawer_layout_login);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view_login);
        navigationView.setNavigationItemSelectedListener(this);

        whatShow();
    }

    // Esto es para cuando se pulse en navigation drawer (para desplegarlo)
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_login);
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

        if (id == R.id.principal) {
            finish();
            i = new Intent(this, MainActivity.class);
        } else if (id == R.id.maps) {
            finish();
            i = new Intent(this, GoogleMaps.class);
        } else if (id == R.id.search_user) {
            finish();
            i = new Intent(this, UserSearch.class);
        } else if (id == R.id.search_estab) {
            finish();
            i = new Intent(this, EstablishmentSearch.class);
        }
        if (i != null)
            startActivity(i);

        DrawerLayout drawer = findViewById(R.id.drawer_layout_login);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Métodos auxiliares --------------------------------------------------------------------------

    private void whatShow(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_login);
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
        nav_Menu.findItem(R.id.login).setVisible(false);
    }

    // Botones -------------------------------------------------------------------------------------

    @OnClick(R.id.log_in)
    public void loginOnClick() {
        NICK = username.getText().toString();
        PASSWORD = passaword.getText().toString();

        if(NICK.isEmpty()|| PASSWORD.isEmpty()){
            Toast.makeText(Login.this,"Introduzca nombre de usaurio y contraseña",
                    Toast.LENGTH_SHORT).show();
        }else {
            mongoAPI("/users/login?nick=" + NICK + "&password=" + PASSWORD, "GET");
        }
    }

    public void continueLogin(){
        if(userLogged != null){
            // Si todu ha ido bien, guardo el id de usuario, su nick y nombre completo
            Preferences.savePreferenceString(Login.this, userLogged.getId(),
                    Preferences.PREFERENCE_USER_LOGIN);
            Preferences.savePreferenceString(Login.this, NICK,
                    Preferences.PREFERENCE_USER_NICK);
            Preferences.savePreferenceString(Login.this, userLogged.getName() + ", " +
                            userLogged.getSurname(), Preferences.PREFERENCE_USER_FULL_NAME);

            Intent i = new Intent(Login.this, MainActivity.class);
            startActivity(i);
            finish(); // Para cerrar la vista actual de inciar sesión.
        } else { // Si el usuario no existe
            Toast.makeText(Login.this,"Nick de usuario o contraseña incorrecto",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.textRegister)
    public void onViewClicked() {
        Intent i = new Intent(Login.this, Register.class);
        startActivity(i);
    }

    // Peticiones a la API -------------------------------------------------------------------------

    public void mongoAPI(String url, String type) {
        final String URL_BASE = "https://api-rest-guia-miguelin-tfg.herokuapp.com/api";
        // Local http://192.168.1.106:1234/
        switch (type) {
            case ("GET"):
                new Login.GetDataTask().execute(URL_BASE + url);
                break;
        }
    }

    // GET -----------------------------------------------------------------------------------------

    @SuppressLint("StaticFieldLeak")
    class GetDataTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(Login.this);
            progressDialog.setMessage("Iniciando sesión...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                return getData(params[0]);
            } catch (IOException ex) {
                return "Error de conexión";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                userLogged = gson.fromJson(result, User.class);
            } catch (Throwable throwable){
                userLogged = new User();
            }

            // Cerrar ventana de diálogo
            if (progressDialog != null) {
                progressDialog.dismiss();
            }

            continueLogin();
        }

        private String getData(String urlPath) throws IOException {
            StringBuilder result = new StringBuilder();
            BufferedReader bufferedReader = null;

            try {
                // Iniciar, configurar solicitud y conectar al servidor
                URL url = new URL(urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000 /* milisegundos */);
                urlConnection.setConnectTimeout(10000 /* milisegundos */);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");// Cabecera de la petición
                urlConnection.connect();

                // Leer datos de respuesta del servidor
                InputStream inputStream = urlConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line).append("\n");
                }

            } finally {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }

            return result.toString();
        }
    }
}
