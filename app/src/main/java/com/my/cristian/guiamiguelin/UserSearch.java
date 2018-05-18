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
import domain.User;

public class UserSearch extends AppCompatActivity implements OnItemClickListener2 {

    @BindView(R.id.search)
    EditText search;
    @BindView(R.id.searchButoon)
    Button searchButoon;
    @BindView(R.id.recycleUsers)
    RecyclerView recycleUsers;

    private static final Gson gson = new Gson();
    private User[] users = null;
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_search);
        ButterKnife.bind(this);
    }

    // Métodos auxiliares --------------------------------------------------------------------------

    @Override
    public void onItemClick(User user) {
        String a = "";
        String b = user.getId().toString();
        for(int i=0; i<adapter.getItemCount(); i++){
            a = adapter.getId(i);

            if(a.contains(b)){
                Toast.makeText(UserSearch.this,"Usuario"+a,
                        Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    private void configAdapter() {
        adapter = new UserAdapter(new ArrayList<User>(), this);
    }

    private void configReclyclerView() {
        recycleUsers.setLayoutManager(new LinearLayoutManager(this));
        recycleUsers.setAdapter(adapter);
    }

    private void generateUser() {

        for (User u : users) {
            adapter.add(u);
        }
    }

    // Botones -------------------------------------------------------------------------------------

    @OnClick(R.id.searchButoon)
    public void searchOnClick() {
        String searchString = search.getText().toString().replaceAll("^\\s*", "");
        searchString = searchString.replaceAll("\\s*$", "");

        if (searchString != "") {
            mongoAPI("/users/search?search=" + searchString, "GET");
        } else {
            Toast.makeText(UserSearch.this, "Introduzca una búsqueda válida",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Peticiones a la API -------------------------------------------------------------------------

    public void mongoAPI(String url, String type) {
        final String URL_BASE = "https://api-rest-guia-miguelin-tfg.herokuapp.com/api";
        // Local http://192.168.1.106:1234/
        switch (type) {
            case ("GET"):
                new UserSearch.GetDataTask1().execute(URL_BASE + url);
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

            progressDialog = new ProgressDialog(UserSearch.this);
            progressDialog.setMessage("Buscado usuarios...");
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

            users = gson.fromJson(result, User[].class);

            configAdapter();
            configReclyclerView();
            generateUser();

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
