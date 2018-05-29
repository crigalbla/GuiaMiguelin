package com.my.cristian.guiamiguelin;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import butterknife.Unbinder;
import domain.User;

public class Login extends Fragment {

    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.passaword)
    EditText passaword;
    @BindView(R.id.log_in)
    Button logIn;
    @BindView(R.id.textRegister)
    TextView textRegister;
    Unbinder unbinder;

    private static final Gson gson = new Gson();
    private User userLogged = null;

    private String NICK = "";
    private String PASSWORD = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.log_in, container, false);

        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // Métodos auxiliares --------------------------------------------------------------------------

    // Botones -------------------------------------------------------------------------------------

    @OnClick(R.id.log_in)
    public void loginOnClick() {
        NICK = username.getText().toString();
        PASSWORD = passaword.getText().toString();

        if(NICK.isEmpty()|| PASSWORD.isEmpty()){
            Toast.makeText(getActivity(),"Introduzca nombre de usaurio y contraseña",
                    Toast.LENGTH_SHORT).show();
        }else {
            mongoAPI("/users/login?nick=" + NICK + "&password=" + PASSWORD, "GET");
        }
    }

    public void continueLogin(){
        if(userLogged != null){
            // Si todu ha ido bien, guardo el id de usuario, su nick y nombre completo
            Preferences.savePreferenceString(getActivity(), userLogged.getId(),
                    Preferences.PREFERENCE_USER_LOGIN);
            Preferences.savePreferenceString(getActivity(), NICK,
                    Preferences.PREFERENCE_USER_NICK);
            Preferences.savePreferenceString(getActivity(), userLogged.getName() + ", " +
                            userLogged.getSurname(), Preferences.PREFERENCE_USER_FULL_NAME);

            Toast.makeText(getActivity(),"Sesión iniciada",
                    Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        } else { // Si el usuario no existe
            Toast.makeText(getActivity(),"Nick de usuario o contraseña incorrecto",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.textRegister)
    public void onViewClicked() {
        getActivity().setTitle("Registrar usuario");
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.contenedor, new Register()).addToBackStack(null).commit();
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

            progressDialog = new ProgressDialog(getActivity());
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
