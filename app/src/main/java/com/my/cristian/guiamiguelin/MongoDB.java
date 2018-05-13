package com.my.cristian.guiamiguelin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MongoDB extends AppCompatActivity {

//    private TextView mResult;
    private AppCompatActivity activity;
    private static final String URL_BASE = "https://api-rest-guia-miguelin-tfg.herokuapp.com/api";

    public void mongoAPI(String url, String type, AppCompatActivity activity){
        this.activity = activity;
        // Local http://192.168.1.106:1234/
        switch (type){
            case("GET"):
                new GetDataTask().execute(URL_BASE + url);
                break;
            case("POST"):
                new PostDataTask().execute(URL_BASE + url);
                break;
            case("PUT"):
                new PutDataTask().execute(URL_BASE + "/status/574400cdd213dde0063a84b9");
                break;
            case("DELETE"):
                new DeleteDataTask().execute(URL_BASE + "/status/5744085cd213dde0063a84ba");
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

            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Cargando datos...");
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

            // Modificar respuesta de datos al texView
//            mResult.setText(result);

            // Cerrar ventana de diálogo
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }

        private String getData(String urlPath) throws IOException {
            StringBuilder result = new StringBuilder();
            BufferedReader bufferedReader =null;

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

    // POST ----------------------------------------------------------------------------------------

    @SuppressLint("StaticFieldLeak")
    class PostDataTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Esto no es mas que una ventana que dice el proceso de la tarea
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Inserting data...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                return postData(params[0]);
            } catch (IOException ex) {
                return "Network error !";
            } catch (JSONException ex) {
                return "Data Invalid !";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // mResult.setText(result);

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
                JSONObject dataToSend = new JSONObject();
                dataToSend.put("fbname", "Nomber de pruebas");
                dataToSend.put("content", "Comentario");
                dataToSend.put("likes", 1);
                dataToSend.put("comments", 1);

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
                while ((line = bufferedReader.readLine()) != null) {
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

            progressDialog = new ProgressDialog(MongoDB.this);
            progressDialog.setMessage("Actualizando datos...");
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

//            mResult.setText(result);

            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }

        private String putData(String urlPath) throws IOException, JSONException {

            BufferedWriter bufferedWriter = null;

            try {
                // Creo los datos a actualizar
                JSONObject dataToSend = new JSONObject();
                dataToSend.put("fbname", "Nombre modificado");
                dataToSend.put("content", "comenatario modificado");
                dataToSend.put("likes", 999);
                dataToSend.put("comments", 999);

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
                bufferedWriter.write(dataToSend.toString());
                bufferedWriter.flush();

                // Comprobar si la actuzalización ha sido correcta
                if (urlConnection.getResponseCode() == 200) {
                    return "Actualización correcta";
                } else {
                    return "Actualización fallida";
                }
            } finally {
                if(bufferedWriter != null) {
                    bufferedWriter.close();
                }
            }
        }
    }

    // DELETE --------------------------------------------------------------------------------------

    @SuppressLint("StaticFieldLeak")
    class DeleteDataTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(MongoDB.this);
            progressDialog.setMessage("Borrando datos...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return deleteData(params[0]);
            } catch (IOException ex) {
                return "Error de conexión";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

//            mResult.setText(result);

            if(progressDialog != null) {
                progressDialog.dismiss();
            }
        }

        private String deleteData(String urlPath) throws IOException {

            String result;

            // Iniciar, configurar solicitud y conectar al servidor
            URL url = new URL(urlPath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(10000 /* milliseconds */);
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setRequestProperty("Content-Type", "application/json");// set header
            urlConnection.connect();

            // Comprobar si la actuzalización ha sido correcta
            if (urlConnection.getResponseCode() == 204) {
                result = "Delete successfully !";
            } else {
                result = "Delete failed !";
            }
            return result;
        }
    }

}
