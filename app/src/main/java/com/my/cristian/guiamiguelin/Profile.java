package com.my.cristian.guiamiguelin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

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
    private User userProfile = null;
    private User userLogged = null;
    private String userIdLogged = null;
    private boolean follow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        ButterKnife.bind(this);

        userIdLogged = Preferences.obtenerPreferenceString(this,
                Preferences.PREFERENCE_USER_LOGIN);

        if(getIntent().getStringExtra("userId") != null){
            mongoAPI("/users/" + getIntent().getStringExtra("userId"),"GET");
        }else{
            mongoAPI("/users/" + userIdLogged, "GET");
        }
    }

    // Botones -------------------------------------------------------------------------------------

    @OnClick(R.id.BTedit)
    public void goToEdit() {
        if(getIntent().getStringExtra("userId") != null){
            if(BTedit.getText().toString().equals("Seguir")){
                follow = true;
                mongoAPI("/users/" + userIdLogged, "FOLLOW-UNFOLLOW");
            }else if(BTedit.getText().toString().equals("Dejar de seguir")){
                follow = false;
                mongoAPI("/users/" + userIdLogged, "FOLLOW-UNFOLLOW");
            }else if(getIntent().getStringExtra("userId").equals(userIdLogged)){
                Intent i = new Intent(Profile.this, EditProfile.class);
                startActivity(i);
            }
        }else{
            Intent i = new Intent(Profile.this, EditProfile.class);
            startActivity(i);
        }
    }

    // Peticiones a la API -------------------------------------------------------------------------

    public void mongoAPI(String url, String type) {
        final String URL_BASE = "https://api-rest-guia-miguelin-tfg.herokuapp.com/api";
        // Local http://192.168.1.106:1234/
        switch (type) {
            case ("GET"):
                new GetDataTask().execute(URL_BASE + url);
                break;
            case ("GET2"):
                new Get2DataTask().execute(URL_BASE + url);
                break;
            case ("FOLLOW-UNFOLLOW"):
                new PutDataTask().execute(URL_BASE + url);
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
                userProfile = gson.fromJson(result, User.class);

                TVnick.setText(userProfile.getNick());
                TVname.setText(userProfile.getName());
                TVsurnames.setText(userProfile.getSurname());

                Integer phone = (userProfile.getPhone() != null)? Integer.valueOf(userProfile.getPhone()) : null;
                String city = userProfile.getCity();
                String email = userProfile.getEmail();
                String pleasures = userProfile.getPleasures();
                String description = userProfile.getDescription();

                if(phone != null)
                    TVphone.setText(phone.toString());
                if(city != null)
                    TVcity.setText(city);
                if(email != null)
                    TVemail.setText(email);
                if(pleasures != null)
                    TVpleasures.setText(pleasures);
                if(description != null)
                    TVdescription.setText(description);

                if(!userIdLogged.equals(userProfile.getId()))
                    if(userIdLogged != null && userIdLogged.length() > 0){
                        // Cojo el usuario que ha iniciado sesión
                        mongoAPI("/users/" + userIdLogged,"GET2");
                    }else {
                        BTedit.setVisibility(View.INVISIBLE);
                    }

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

    // GET2 -----------------------------------------------------------------------------------------

    @SuppressLint("StaticFieldLeak")
    class Get2DataTask extends AsyncTask<String, Void, String> {

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

                if(userLogged.getFolloweds().contains(getIntent().getStringExtra("userId"))){
                    BTedit.setText("Dejar de seguir");
                }else{
                    BTedit.setText("Seguir");
                }

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

    // PUT -----------------------------------------------------------------------------------------

    @SuppressLint("StaticFieldLeak")
    class PutDataTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(Profile.this);
            progressDialog.setMessage("Enviando petición...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return putData(params[0]);
            } catch (IOException ex) {
                return "Error de conexión";
            } catch (JSONException ex) {
                return "Datos incorrectos";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(follow){
                BTedit.setText("Dejar de seguir");
                Toast.makeText(Profile.this, "Has comenzado a seguir a " + userProfile.getNick(),
                        Toast.LENGTH_LONG).show();
            }else{
                BTedit.setText("Seguir");
                Toast.makeText(Profile.this, "Has dejado de seguir a " + userProfile.getNick(),
                        Toast.LENGTH_LONG).show();
            }

            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }

        private String putData(String urlPath) throws IOException, JSONException {

            BufferedWriter bufferedWriter = null;

            try {
                // Creo los datos a actualizar
                List<String> followeds = userLogged.getFolloweds();
                if(follow){
                    followeds.add(userProfile.getId());
                }else{
                    followeds.remove(userProfile.getId());
                }
                userLogged.setFolloweds(followeds);

                User userLoggedPruned = new User();
                userLoggedPruned.setFolloweds(userLogged.getFolloweds());
                String dataToSend = gson.toJson(userLoggedPruned); // Le paso un objeto menos pesado

                // Iniciar, configurar solicitud y conectar al servidor
                URL url = new URL(urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(10000 /* milliseconds */);
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoOutput(true);  //enable output (body data)
                urlConnection.setRequestProperty("Content-Type", "application/json");// set header
                urlConnection.connect();

                // Escribo los datos en el servidor
                OutputStream outputStream = urlConnection.getOutputStream();
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                bufferedWriter.write(dataToSend);
                bufferedWriter.flush();

                // Comprobar si la actuzalización ha sido correcta
                if (urlConnection.getResponseCode() == 200) {
                    return "Actualización correcta";
                } else {
                    return "Actualización fallida";
                }
            } finally {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            }
        }
    }
}
