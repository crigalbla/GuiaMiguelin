package com.my.cristian.guiamiguelin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
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

public class Profile extends AppCompatActivity {

    @BindView(R.id.TVnick)
    TextView TVnick;
    @BindView(R.id.TVname)
    TextView TVname;
    @BindView(R.id.TVsurnames)
    TextView TVsurnames;
    @BindView(R.id.TVphone)
    TextView TVphone;
    @BindView(R.id.TVcity)
    TextView TVcity;
    @BindView(R.id.TVemail)
    TextView TVemail;
    @BindView(R.id.TVpleasures)
    TextView TVpleasures;
    @BindView(R.id.TVdescription)
    TextView TVdescription;
    @BindView(R.id.BTedit)
    Button BTedit;

    private static final Gson gson = new Gson();
    private User userLogged = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        ButterKnife.bind(this);

        //TODO hacer que se puedan mostrar otros perfiles con esta vista

        mongoAPI("/users/" + Preferences.obtenerPreferenceString(this,
                Preferences.PREFERENCE_USER_LOGIN), "GET");
    }

    @OnClick(R.id.BTedit)
    public void goToEdit() {
        Intent i = new Intent(Profile.this, EditProfile.class);
        startActivity(i);
    }

    // Peticiones a la API -------------------------------------------------------------------------

    public void mongoAPI(String url, String type) {
        final String URL_BASE = "https://api-rest-guia-miguelin-tfg.herokuapp.com/api";
        // Local http://192.168.1.106:1234/
        switch (type) {
            case ("GET"):
                new GetDataTask().execute(URL_BASE + url);
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

            progressDialog = new ProgressDialog(Profile.this);
            progressDialog.setMessage("Obteniendo perfil...");
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

                TVnick.setText(userLogged.getNick());
                TVname.setText(userLogged.getName());
                TVsurnames.setText(userLogged.getSurname());

                Integer phone = (userLogged.getPhone() != null)? new Integer(userLogged.getPhone()) : null;
                String city = (userLogged.getCity() != null)? userLogged.getCity() : null;
                String email = (userLogged.getEmail() != null)? userLogged.getEmail() : null;
                String pleasures = (userLogged.getPleasures() != null)? userLogged.getPleasures() : null;
                String description = (userLogged.getDescription() != null)? userLogged.getDescription() : null;

                if(phone != null)
                    TVphone.setText(phone);
                if(city != null)
                    TVcity.setText(city);
                if(email != null)
                    TVemail.setText(email);
                if(pleasures != null)
                    TVpleasures.setText(pleasures);
                if(description != null)
                    TVdescription.setText(description);
            } catch (Throwable throwable) {
                Toast.makeText(Profile.this, "Ha habido un problema con la aplicación",
                        Toast.LENGTH_LONG).show();
            }

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
