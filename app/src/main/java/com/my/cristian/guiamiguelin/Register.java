package com.my.cristian.guiamiguelin;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import domain.User;

public class Register extends Fragment {

    @BindView(R.id.send)
    Button send;
    @BindView(R.id.nick)
    TextInputEditText nick;
    @BindView(R.id.name)
    TextInputEditText name;
    @BindView(R.id.surnames)
    TextInputEditText surnames;
    @BindView(R.id.password)
    TextInputEditText password;
    @BindView(R.id.verifyPassword)
    TextInputEditText verifyPassword;
    @BindView(R.id.telephone)
    TextInputEditText telephone;
    @BindView(R.id.city)
    TextInputEditText city;
    @BindView(R.id.email)
    TextInputEditText email;
    @BindView(R.id.pleasures)
    TextInputEditText pleasures;
    @BindView(R.id.description)
    TextInputEditText description;
    @BindView(R.id.tNick)
    TextInputLayout tNick;
    @BindView(R.id.tName)
    TextInputLayout tName;
    @BindView(R.id.tSurnames)
    TextInputLayout tSurnames;
    @BindView(R.id.tPassword)
    TextInputLayout tPassword;
    @BindView(R.id.tVerfyPassword)
    TextInputLayout tVerfyPassword;
    Unbinder unbinder;

    private static final Gson gson = new Gson();
    private User newUser = new User();
    private User existUser = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.register, container, false);

        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // Métodos auxiliares --------------------------------------------------------------------------

    private boolean validations(TextInputEditText text, TextInputLayout layout, String error){
        if(text.getText().toString().trim().isEmpty()){
            layout.setError(error);
            return false;
        }else {
            layout.setErrorEnabled(false);
        }
        return true;
    }

    // Botones -------------------------------------------------------------------------------------

    @OnClick({R.id.send, R.id.BTcancelRegister})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.send:
                if(validations(nick, tNick, "Rellenar nombre de usuario") &&
                        validations(name, tName, "Rellenar nombre") &&
                        validations(surnames, tSurnames, "Rellenar apellidos") &&
                        validations(password, tPassword, "Rellenar contraseña") &&
                        validations(verifyPassword, tVerfyPassword,
                                "Rellenar verificación de contraseña")){

                    if (password.getText().toString().equals(verifyPassword.getText().toString())) {
                        mongoAPI("/users/exist?nick=" + nick.getText().toString(), "GET");
                    } else {
                        Toast.makeText(getActivity(),
                                "La contraseña y la verificación de contraseña no son iguales",
                                Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.BTcancelRegister:
                getActivity().onBackPressed();
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
            case ("POST"):
                new PostDataTask().execute(URL_BASE + url);
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

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Verificando datos...");
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

            try{
                existUser = gson.fromJson(result, User.class);
                if (existUser == null) {
                    mongoAPI("/users/", "POST");
                } else {
                    Toast.makeText(getActivity(),
                            "El nombre de usuario " + existUser.getNick() + " ya existe",
                            Toast.LENGTH_LONG).show();
                }
            } catch (Throwable throwable) {
                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
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

    // POST ----------------------------------------------------------------------------------------

    @SuppressLint("StaticFieldLeak")
    class PostDataTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Esto no es mas que una ventana que dice el proceso de la tarea
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Creando usuario...");
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

            if(result != "Error de conexión" && result != "Datos incorrectos"){
                Toast.makeText(getActivity(), "Usuario creado", Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
            }else{
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            }

            // Cerrar ventana de diálogo
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
                newUser.setNick(nick.getText().toString());
                newUser.setPassword(password.getText().toString());
                newUser.setName(name.getText().toString());
                newUser.setSurname(surnames.getText().toString());
                if(description.getText().toString().trim().length() > 0)
                    newUser.setDescription(description.getText().toString());
                if(pleasures.getText().toString().trim().length() > 0)
                    newUser.setPleasures(pleasures.getText().toString());
                if(city.getText().toString().trim().length() > 0)
                    newUser.setCity(city.getText().toString());
                if(email.getText().toString().trim().length() > 0)
                    newUser.setEmail(email.getText().toString());
                if(telephone.getText().toString().trim().length() > 0)
                    newUser.setPhone(new Integer(telephone.getText().toString()));
                newUser.setReviews(new ArrayList<String>());
                newUser.setFolloweds(new ArrayList<String>());

                String dataToSend = gson.toJson(newUser);

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
}
