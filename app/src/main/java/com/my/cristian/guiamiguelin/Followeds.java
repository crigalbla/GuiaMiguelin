package com.my.cristian.guiamiguelin;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import butterknife.Unbinder;
import domain.FollowedsDomain;
import domain.User;


public class Followeds extends Fragment implements OnItemClickListener2 {

    @BindView(R.id.recycleFolloweds)
    RecyclerView recycleFolloweds;
    @BindView(R.id.notFolloweds)
    TextView notFolloweds;
    Unbinder unbinder;

    private static final Gson gson = new Gson();
    private User[] users = null;
    private String result2 = null;

    private UserAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.followeds, container, false);

        unbinder = ButterKnife.bind(this, view);
        //Esto es para que recargue la busqueda cuando le doy al botón back
        if (result2 != null && result2.length() > 0) {
            FollowedsDomain followeds = gson.fromJson(result2, FollowedsDomain.class);
            users = followeds.getFolloweds();
            configAdapter();
            configReclyclerView();
            generateUser();
        } else {
            Object userId = getArguments() != null ? getArguments().get("userId") : null;
            mongoAPI("/users/followeds/" + userId, "GET");
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // Métodos auxiliares --------------------------------------------------------------------------

    @Override
    public void onItemClick(User user) {
        String a = "";
        String b = user.getId().toString();
        for (int i = 0; i < adapter.getItemCount(); i++) {
            a = adapter.getId(i);

            if (a.contains(b)) {
                getActivity().setTitle("Perfil de usuario");
                Fragment fragment = new Profile();
                Bundle args = new Bundle();
                args.putString("userId", a);
                fragment.setArguments(args);
                getActivity().getFragmentManager().beginTransaction()
                        .replace(R.id.contenedor, fragment).addToBackStack(null).commit();
                break;
            }
        }
    }

    private void configAdapter() {
        adapter = new UserAdapter(new ArrayList<User>(), this);
    }

    private void configReclyclerView() {
        recycleFolloweds.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleFolloweds.setAdapter(adapter);
    }

    private void generateUser() {

        for (User u : users) {
            adapter.add(u);
        }
        if(users.length == 0)
            notFolloweds.setVisibility(View.VISIBLE);
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

    // GET users ------------------------------------------------------------------------------------

    @SuppressLint("StaticFieldLeak")
    class GetDataTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Buscado usuarios seguidos...");
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

            // Guardo el resultado ya que me hará falta cuando pulse el botón back para no hacer
            // de nuevo la búsqueda.
            result2 = result;

            FollowedsDomain followeds = gson.fromJson(result, FollowedsDomain.class);
            users = followeds.getFolloweds();

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
