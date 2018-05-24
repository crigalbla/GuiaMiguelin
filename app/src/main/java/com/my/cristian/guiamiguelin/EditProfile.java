package com.my.cristian.guiamiguelin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import domain.User;

public class EditProfile extends AppCompatActivity {

    @BindView(R.id.etName)
    TextInputEditText etName;
    @BindView(R.id.etSurnames)
    TextInputEditText etSurnames;
    @BindView(R.id.etEmail)
    TextInputEditText etEmail;
    @BindView(R.id.etPhone)
    TextInputEditText etPhone;
    @BindView(R.id.etCity)
    TextInputEditText etCity;
    @BindView(R.id.etPleasures)
    TextInputEditText etPleasures;
    @BindView(R.id.etDescription)
    TextInputEditText etDescription;
    @BindView(R.id.BTsend)
    Button BTsend;
    @BindView(R.id.BTcancelEdit)
    Button BTcancelEdit;
    @BindView(R.id.tName)
    TextInputLayout tName;
    @BindView(R.id.tSurnames)
    TextInputLayout tSurnames;

    private static final Gson gson = new Gson();
    private User userLogged = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        ButterKnife.bind(this);

        mongoAPI("/users/" + Preferences.obtenerPreferenceString(this,
                Preferences.PREFERENCE_USER_LOGIN), "GET");
    }

    // Métodos auxiliares --------------------------------------------------------------------------

    private boolean validations(TextInputEditText text, TextInputLayout layout, String error) {
        if (text.getText().toString().trim().isEmpty()) {
            layout.setError(error);
            return false;
        } else {
            layout.setErrorEnabled(false);
        }
        return true;
    }

    // Botones -------------------------------------------------------------------------------------

    @OnClick({R.id.BTsend, R.id.BTcancelEdit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.BTsend:
                if(validations(etName, tName, "Rellenar nombre") &&
                        validations(etSurnames, tSurnames, "Rellenar apellidos")){
                    mongoAPI("/users/" + Preferences.obtenerPreferenceString(this,
                            Preferences.PREFERENCE_USER_LOGIN), "PUT");
                    Intent i = new Intent(this, Profile.class);
                    startActivity(i);
                }
                break;
            case R.id.BTcancelEdit:
                finish();
                break;
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
            case ("PUT"):
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

            progressDialog = new ProgressDialog(EditProfile.this);
            progressDialog.setMessage("Cargando perfil...");
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

            userLogged = gson.fromJson(result, User.class);
            if (userLogged != null) {
                etName.setText(userLogged.getName());
                etSurnames.setText(userLogged.getSurname());
                Integer phone = (userLogged.getPhone() != null) ? new Integer(userLogged.getPhone()) : null;
                String city = (userLogged.getCity() != null) ? userLogged.getCity() : null;
                String email = (userLogged.getEmail() != null) ? userLogged.getEmail() : null;
                String pleasures = (userLogged.getPleasures() != null) ? userLogged.getPleasures() : null;
                String description = (userLogged.getDescription() != null) ? userLogged.getDescription() : null;

                if (phone != null)
                    etPhone.setText(phone.toString());
                if (city != null)
                    etCity.setText(city);
                if (email != null)
                    etEmail.setText(email);
                if (pleasures != null)
                    etPleasures.setText(pleasures);
                if (description != null)
                    etDescription.setText(description);
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

            progressDialog = new ProgressDialog(EditProfile.this);
            progressDialog.setMessage("Enviando datos...");
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

            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }

        private String putData(String urlPath) throws IOException, JSONException {

            BufferedWriter bufferedWriter = null;

            try {
                // Creo los datos a actualizar
                userLogged.setName(etName.getText().toString());
                userLogged.setSurname(etSurnames.getText().toString());
                userLogged.setCity(etCity.getText().toString());
                userLogged.setEmail(etEmail.getText().toString());
                if (!etPhone.getText().toString().isEmpty())
                    userLogged.setPhone(new Integer(etPhone.getText().toString()));
                userLogged.setPleasures(etPleasures.getText().toString());
                userLogged.setDescription(etDescription.getText().toString());

                String dataToSend = gson.toJson(userLogged);

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

//    public List<User> readJsonStream(InputStream in) throws IOException {
//        // Nueva instancia JsonReader
//        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
//        try {
//            // Leer Array
//            return leerArray(reader);
//        } finally {
//            reader.close();
//        }
//    }
//
//    public List leerArray(JsonReader reader) throws IOException {
//        // Lista temporal
//        ArrayList users = new ArrayList();
//
//        reader.beginArray();
//        while (reader.hasNext()) {
//            // Leer objeto
//            users.add(leer(reader));
//        }
//        reader.endArray();
//        return users;
//    }
//
//    public User leer(JsonReader reader) throws IOException {
//        String nick;
//        String passaword;
//        String name;
//        String surname;
//        String description;
//        String pleasures;
//        String city;
//        String email;
//        Integer phone;
//        List<User> followeds;
//        List<Review> reviews;
//
//        reader.beginObject();
//        while (reader.hasNext()) {
//            String nameJson = reader.nextName();
//            switch (nameJson) {
//                case "name":
//                    name = reader.nextString();
//                    break;
//                case "surname":
//                    surname = reader.nextString();
//                    break;
//                case "email":
//                    email = reader.nextString();
//                    break;
//                case "phone":
//                    phone = reader.nextInt();
//                    break;
//                case "city":
//                    city = reader.nextString();
//                    break;
//                case "pleasures":
//                    pleasures = reader.nextString();
//                    break;
//                case "description":
//                    description = reader.nextString();
//                    break;
//                default:
//                    reader.skipValue();
//                    break;
//            }
//        }
//        reader.endObject();
//        return new User();
//    }
}
