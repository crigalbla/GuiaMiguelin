package com.my.cristian.guiamiguelin;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import butterknife.Unbinder;
import domain.Pub;
import domain.Restaurant;
import domain.Review;
import domain.User;

public class CreateReview extends Fragment {

    @BindView(R.id.reviewEstabl)
    TextView reviewEstabl;
    @BindView(R.id.reviewPuntuation)
    EditText reviewPuntuation;
    @BindView(R.id.reviewCommet)
    TextInputEditText reviewCommet;
    Unbinder unbinder;

    private static final Gson gson = new Gson();
    private Review newReview = new Review();
    private Pub putPub = new Pub();
    private Restaurant putRestaurant = new Restaurant();
    private User putUser = new User();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.create_review, container, false);

        unbinder = ButterKnife.bind(this, view);
        String nombreEstablecimiento = getArguments() != null ?
                (String) getArguments().get("nombre_establecimiento") : null;
        reviewEstabl.setText(nombreEstablecimiento);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // Métodos auxiliares --------------------------------------------------------------------------

    // Botones -------------------------------------------------------------------------------------

    @OnClick({R.id.sendReview, R.id.cancelReview})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sendReview:
                if (reviewPuntuation.getText().toString().length() == 0) {
                    Toast.makeText(getActivity(), "Introduzca una puntuación",
                            Toast.LENGTH_SHORT).show();
                } else if (reviewPuntuation.getText().toString().length() > 1){
                    Toast.makeText(getActivity(), "La puntuación debe de ser de 0 a 5",
                            Toast.LENGTH_LONG).show();
                } else {
                    mongoAPI("/reviews/", "POST");
                }
                break;
            case R.id.cancelReview:
                getActivity().onBackPressed();
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
            progressDialog = new ProgressDialog(getActivity());
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

            String tipo = getArguments() != null ?
                    (String) getArguments().get("tipo") : null;
            String idEstablecimiento = getArguments() != null ?
                    (String) getArguments().get("id_establecimiento") : null;

            mongoAPI(tipo + idEstablecimiento, "ESTABLISHMENT");

            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }

        private String postData(String urlPath) throws IOException, JSONException {

            StringBuilder result = new StringBuilder();
            BufferedWriter bufferedWriter = null;
            BufferedReader bufferedReader = null;

            try {
                String idEstablecimiento = getArguments() != null ?
                        (String) getArguments().get("id_establecimiento") : null;

                // Creo datos para enviarlos al servidor
                newReview.setAuthor(Preferences.obtenerPreferenceString(getActivity(),
                        Preferences.PREFERENCE_USER_LOGIN));
                newReview.setEstablishment(idEstablecimiento);
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

            progressDialog = new ProgressDialog(getActivity());
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

            mongoAPI("/users/" + Preferences.obtenerPreferenceString(getActivity(),
                    Preferences.PREFERENCE_USER_LOGIN), "GET-USER");

            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }

        private String putData(String urlPath) throws IOException, JSONException {

            BufferedWriter bufferedWriter = null;

            try {
                String reseñas = getArguments() != null ?
                        (String) getArguments().get("reseñas") : null;
                String notaMedia = getArguments() != null ?
                        (String) getArguments().get("nota_media") : null;
                String tipo = getArguments() != null ?
                        (String) getArguments().get("tipo") : null;
                // Creo los datos a actualizar
                String dataToSend = "";
                List<String> reviews = new ArrayList<String>();
                Double newAverage = newReview.getPuntuation().doubleValue();
                // La lista de reseñas vacía solo contiene [], asi que compruebo eso
                if(reseñas.length() > 2){ // TODO esto no funciona del todo bien, corregir
                    reviews.addAll(Arrays.asList(reseñas
                            .replace("[", "").replace("]", "")
                            .replace(" ", "")
                            .split(",")));
                    Integer puntuation = newReview.getPuntuation();
                    Double average = new Double(notaMedia);
                    Integer numberOfReviews = reviews.size();
                    Double variable = average / numberOfReviews;
                    newAverage = variable*(numberOfReviews-1) + puntuation/numberOfReviews;
                    DecimalFormat df = new DecimalFormat("#.##");
                    newAverage = new Double( df.format(newAverage).replace(",", ".") );
                }
                reviews.add(newReview.getId());

                if(tipo.equals("/pubs/")){
                    putPub.setReviews(reviews);
                    putPub.setAverage(newAverage);
                    dataToSend = gson.toJson(putPub);
                }
                if(tipo.equals("/restaurants/")){
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

            progressDialog = new ProgressDialog(getActivity());
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

            mongoAPI("/users/" + Preferences.obtenerPreferenceString(getActivity(),
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

            progressDialog = new ProgressDialog(getActivity());
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

            // Para terminar volemos a la actividad anterior, ahora debe de aparecer la reseña
            getActivity().onBackPressed();

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
