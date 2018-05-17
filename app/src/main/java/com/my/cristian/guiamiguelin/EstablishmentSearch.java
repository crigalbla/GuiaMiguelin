package com.my.cristian.guiamiguelin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import domain.Establishment;
import domain.Pub;
import domain.Restaurant;

public class EstablishmentSearch extends AppCompatActivity implements OnItemClickListener {

    @BindView(R.id.search)
    EditText search;
    @BindView(R.id.searchButoon)
    Button searchButoon;
    @BindView(R.id.recyclePubs)
    RecyclerView recyclePubs;
    @BindView(R.id.recycleRestaurants)
    RecyclerView recycleRestaurants;
    @BindView(R.id.layoutRestaurant)
    LinearLayout layoutRestaurant;

    private static final Gson gson = new Gson();

    private Pub[] pubs = null;
    private Restaurant[] restaurants = null;

    private EstablishmentAdapter adapter1;
    private EstablishmentAdapter adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.establishment_search);
        ButterKnife.bind(this);
    }

    // Métodos auxiliares --------------------------------------------------------------------------

    @Override
    public void onItemClick(Establishment establishment) {

    }

    @Override
    public void onItemLongClick(Establishment establishment) {

    }

    private void configAdapter() {
        adapter1 = new EstablishmentAdapter(new ArrayList<Establishment>(), this);
        adapter2 = new EstablishmentAdapter(new ArrayList<Establishment>(), this);
    }

    private void configReclyclerView() {
        recyclePubs.setLayoutManager(new LinearLayoutManager(this));
        recyclePubs.setAdapter(adapter1);

        recycleRestaurants.setLayoutManager(new LinearLayoutManager(this));
        recycleRestaurants.setAdapter(adapter2);
    }

    private void generateEstablishment() {

        for (Pub p : pubs) {
            adapter1.add(p);
        }
        for (Restaurant r : restaurants) {
            adapter2.add(r);
        }
    }

    // Botones -------------------------------------------------------------------------------------

    @OnClick(R.id.searchButoon)
    public void onViewClicked() {
        String searchString = search.getText().toString().replaceAll("^\\s*", "");
        searchString = searchString.replaceAll("\\s*$", "");

        if (searchString != "") {
            mongoAPI("/pubs/search?search=" + searchString, "pub");
            mongoAPI("/restaurants/search?search=" + searchString, "restaurant");
        } else {
            Toast.makeText(EstablishmentSearch.this, "Introduzca una búsqueda válida",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Peticiones a la API -------------------------------------------------------------------------

    public void mongoAPI(String url, String type) {
        final String URL_BASE = "https://api-rest-guia-miguelin-tfg.herokuapp.com/api";
        // Local http://192.168.1.106:1234/
        switch (type) {
            case ("pub"):
                new GetDataTask1().execute(URL_BASE + url);
                break;
            case ("restaurant"):
                new GetDataTask2().execute(URL_BASE + url);
                break;
        }
    }

    // GET pubs ------------------------------------------------------------------------------------

    @SuppressLint("StaticFieldLeak")
    class GetDataTask1 extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(EstablishmentSearch.this);
            progressDialog.setMessage("Buscado establecimientos...");
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

            pubs = gson.fromJson(result, Pub[].class);

            // Cerrar ventana de diálogo
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
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

    // GET restaurants -----------------------------------------------------------------------------

    @SuppressLint("StaticFieldLeak")
    class GetDataTask2 extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(EstablishmentSearch.this);
            progressDialog.setMessage("Buscado establecimientos...");
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

            restaurants = gson.fromJson(result, Restaurant[].class);

            configAdapter();
            configReclyclerView();
            generateEstablishment();

            // Cerrar ventana de diálogo
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
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
