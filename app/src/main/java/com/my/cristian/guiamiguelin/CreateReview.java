package com.my.cristian.guiamiguelin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import domain.Pub;
import domain.Restaurant;
import domain.Review;
import domain.User;

public class CreateReview extends AppCompatActivity {

    @BindView(R.id.reviewEstabl)
    TextView reviewEstabl;
    @BindView(R.id.reviewPuntuation)
    EditText reviewPuntuation;
    @BindView(R.id.reviewCommet)
    TextInputEditText reviewCommet;

    private static final Gson gson = new Gson();
    private Review newReview = new Review();
    private Pub putPub = new Pub();
    private Restaurant putRestaurant = new Restaurant();
    private User putUser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_review);
        ButterKnife.bind(this);

        reviewEstabl.setText(getIntent().getStringExtra("nombre_establecimiento"));
    }

    // Métodos auxiliares --------------------------------------------------------------------------

    // Botones -------------------------------------------------------------------------------------

    @OnClick({R.id.sendReview, R.id.cancelReview})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sendReview:
                if (reviewPuntuation.getText().toString().length() == 0) {
                    Toast.makeText(this, "Introduzca una puntuación",
                            Toast.LENGTH_SHORT).show();
                } else if (reviewPuntuation.getText().toString().length() > 1){
                    Toast.makeText(this, "La puntuación debe de ser de 0 a 5",
                            Toast.LENGTH_LONG).show();
                } else {
                    mongoAPI("/reviews/", "POST");
                }
                break;
            case R.id.cancelReview:
                finish();
                break;
        }
    }

    // Peticiones a la API -------------------------------------------------------------------------

    public void mongoAPI(String url, String type) {
        final String URL_BASE = "https://api-rest-guia-miguelin-tfg.herokuapp.com/api";
        // Local http://192.168.1.106:1234/
        switch (type) {
            case ("POST"):
                new CreateReview.PostDataTask().execute(URL_BASE + url);
                break;
            case ("ESTABLISHMENT"):
                new CreateReview.PutEstablishmentDataTask().execute(URL_BASE + url);
                break;
            case ("GET-USER"):
                new CreateReview.GetUserDataTask().execute(URL_BASE + url);
                break;
            case ("PUT-USER"):
                new CreateReview.PutUserDataTask().execute(URL_BASE + url);
                break;
        }
    }

    // POST ----------------------------------------------------------------------------------------

    @SuppressLint("StaticFieldLeak")
    class PostDataTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Esto no es mas que una ventana que dice el proceso de la tarea
            progressDialog = new ProgressDialog(CreateReview.this);
            progressDialog.setMessage("Enviando reseña...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                return postData(params[0]);
            } catch (IOException ex) {
                return "Error de conexión";
            } catch (JSONException ex) {
                return "Datos incorrectos";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            mongoAPI(getIntent().getStringExtra("tipo") +
                    getIntent().getStringExtra("id_establecimiento"), "ESTABLISHMENT");

            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }

        private String postData(String urlPath) throws IOException, JSONException {

            StringBuilder result = new StringBuilder();
            BufferedWriter bufferedWriter = null;
            BufferedReader bufferedReader = null;

            try {
                // Creo datos para enviarlos al servidor
                newReview.setAuthor(Preferences.obtenerPreferenceString(CreateReview.this,
                        Preferences.PREFERENCE_USER_LOGIN));
                newReview.setEstablishment(getIntent().getStringExtra("id_establecimiento"));
                newReview.setPuntuation(new Integer(reviewPuntuation.getText().toString()) );
                if(reviewCommet.getText().toString().length() > 0)
                    newReview.setComment(reviewCommet.getText().toString());

                String dataToSend = gson.toJson(newReview);

                // Iniciar, configurar solicitud y conectar al servidor
                URL url = new URL(urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(10000 /* milliseconds */);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);  //enable output (body data)
                urlConnection.setRequestProperty("Content-Type", "application/json");// set header
                urlConnection.connect();

                // Escribo los datos en el servidor
                OutputStream outputStream = urlConnection.getOutputStream();
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                bufferedWriter.write(dataToSend.toString());
                bufferedWriter.flush();

                // Leo la respuesta del servidor
                InputStream inputStream = urlConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) { // bufferedReader.readLine() de aquí puedo sacar el id
                    result.append(line).append("\n");
                }
            } finally {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            }

            // Cojo el objeto que se acaba de guardar
            newReview = gson.fromJson(result.toString(), Review.class);
            return result.toString();
        }
    }

    // PUT establishment ---------------------------------------------------------------------------

    @SuppressLint("StaticFieldLeak")
    class PutEstablishmentDataTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(CreateReview.this);
            progressDialog.setMessage("Enviando reseña...");
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

            mongoAPI("/users/" + Preferences.obtenerPreferenceString(CreateReview.this,
                    Preferences.PREFERENCE_USER_LOGIN), "GET-USER");

            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }

        private String putData(String urlPath) throws IOException, JSONException {

            BufferedWriter bufferedWriter = null;

            try {
                // Creo los datos a actualizar
                String dataToSend = "";
                List<String> reviews = new ArrayList<String>();
                Double newAverage = newReview.getPuntuation().doubleValue();
                // La lista de reseñas vacía solo contiene [], asi que compruebo eso
                if(getIntent().getStringExtra("reseñas").length() > 2){
                    reviews.addAll(Arrays.asList(getIntent().getStringExtra("reseñas")
                            .replace("[", "").replace("]", "")
                            .replace(" ", "")
                            .split(",")));
                    Integer puntuation = newReview.getPuntuation();
                    Double average = new Double( getIntent().getStringExtra("nota_media") );
                    Integer numberOfReviews = reviews.size();
                    Double variable = average / numberOfReviews;
                    newAverage = variable*(numberOfReviews-1) + puntuation/numberOfReviews;
                    DecimalFormat df = new DecimalFormat("#.##");
                    newAverage = new Double( df.format(newAverage).replace(",", ".") );
                }
                reviews.add(newReview.getId());

                if(getIntent().getStringExtra("tipo").equals("/pubs/")){
                    putPub.setReviews(reviews);
                    putPub.setAverage(newAverage);
                    dataToSend = gson.toJson(putPub);
                }
                if(getIntent().getStringExtra("tipo").equals("/restaurants/")){
                    putRestaurant.setReviews(reviews);
                    putRestaurant.setAverage(newAverage);
                    dataToSend = gson.toJson(putRestaurant);
                }

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

    // GET user ------------------------------------------------------------------------------------

    @SuppressLint("StaticFieldLeak")
    class GetUserDataTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(CreateReview.this);
            progressDialog.setMessage("Enviando reseña...");
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

            User user = gson.fromJson(result, User.class);
            putUser.setReviews(user.getReviews());  // Solo me hacen falta las reviews

            mongoAPI("/users/" + Preferences.obtenerPreferenceString(CreateReview.this,
                    Preferences.PREFERENCE_USER_LOGIN), "PUT-USER");

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

    // PUT user ------------------------------------------------------------------------------------

    @SuppressLint("StaticFieldLeak")
    class PutUserDataTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(CreateReview.this);
            progressDialog.setMessage("Enviando reseña...");
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

            Intent i = new Intent(CreateReview.this, ShowEstablishment.class);
            String cadena = null;
            // Si tiene por ejemplo la media, es que es ese tipo de establecimiento el que se ah editado
            if(putPub.getAverage() != null)
                cadena = "pubId";
            if(putRestaurant.getAverage() != null)
                cadena = "restaurantId";

            finish();
            i.putExtra(cadena, newReview.getEstablishment());
            startActivity(i);

            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }

        private String putData(String urlPath) throws IOException, JSONException {

            BufferedWriter bufferedWriter = null;

            try {
                // Creo los datos a actualizar
                List<String> reviews = new ArrayList<String>(putUser.getReviews());
                reviews.add(newReview.getId());
                putUser.setReviews(reviews);

                String dataToSend = gson.toJson(putUser);

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
