package com.my.cristian.guiamiguelin;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import domain.Establishment;
import domain.User;


public class ContentMain extends Fragment implements OnItemClickListener {

    @BindView(R.id.recycle)
    RecyclerView recycle;
    Unbinder unbinder;

    private static final Gson gson = new Gson();
    private User userLogged = null;
    private Establishment[] pubs = null;
    private Establishment[] restaurants = null;
    private String average = "4";
    private Integer numberPubsOnAdapter = 0;

    private EstablishmentAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        whatShow(); // Configuro el drawer para lo que debe mostrar y lo que no por defecto
        View view = inflater.inflate(R.layout.content_main, container, false);

        unbinder = ButterKnife.bind(this, view);
        if(pubs == null && restaurants == null) {
            recomendationSystem();
        }else {
            configAdapter();
            configReclyclerView();
            generateEstablishment();
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // Botones -------------------------------------------------------------------------------------

    // Métodos auxiliares --------------------------------------------------------------------------

    @Override
    public void onItemClick(Establishment establishment) {
        String a = "";
        String b = establishment.getId().toString();

        for(int i=0; i<adapter.getItemCount(); i++){
            a = adapter.getId(i);

            if(a.contains(b)){
                Fragment fragment = new ShowEstablishment();
                Bundle args = new Bundle();
                if(i <= numberPubsOnAdapter-1){ // Para identificar el tipo de establecimiento
                    getActivity().setTitle("Bar");
                    args.putString("pubId", a);
                    fragment.setArguments(args);
                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.contenedor, fragment).addToBackStack(null).commit();
                }else {
                    getActivity().setTitle("Restaurante");
                    args.putString("restaurantId", a);
                    fragment.setArguments(args);
                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.contenedor, fragment).addToBackStack(null).commit();
                }
                break;
            }
        }
    }

    private void configAdapter() {
        adapter = new EstablishmentAdapter(new ArrayList<Establishment>(), this);
    }

    private void configReclyclerView() {
        recycle.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycle.setAdapter(adapter);
    }

    private void generateEstablishment() {
        numberPubsOnAdapter = 0;
        Integer countPubs = pubs.length;
        Integer countRestaurants = restaurants.length;
        // Ordeno los establecimientos dando importanta a la media de valoraciones
        Arrays.sort(pubs);
        Arrays.sort(restaurants);

        for (int i = 0; i < countPubs; i++) {
            if(i < 2) {
                adapter.add(pubs[i]);
                numberPubsOnAdapter++;
            }
            if(i >= 2 && i < 4-countRestaurants) {
                adapter.add(pubs[i]);
                numberPubsOnAdapter++;
            }
        }
        for (int i = 0; i < countRestaurants; i++) {
            if(i < 2)
                adapter.add(restaurants[i]);
            if(i >= 2 && i < 4-countPubs)
                adapter.add(restaurants[i]);
        }
    }

    private List<Establishment> recomendationSystem(){
        List<Establishment> recomended = new ArrayList<Establishment>();
        String userIdLogged = Preferences.obtenerPreferenceString(getActivity(),
                Preferences.PREFERENCE_USER_LOGIN);

        if(userIdLogged != null && userIdLogged.length() > 0){
            mongoAPI("/users/" + userIdLogged, "GET");
        }else{
            mongoAPI("/pubs/mostPopular?average=" + average, "ESTABLISHMENTS");
        }
        return recomended;
    }

    private void whatShow() {
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        View viewHeader = navigationView.getHeaderView(0);
        Menu nav_Menu = navigationView.getMenu();

        ImageView imageProfile = (ImageView) viewHeader.findViewById(R.id.imageProfile);
        TextView nickProfile = (TextView) viewHeader.findViewById(R.id.nickProfile);
        TextView fullNameProfile = (TextView) viewHeader.findViewById(R.id.fullNameProfile);

        imageProfile.setVisibility(View.INVISIBLE);
        nickProfile.setVisibility(View.INVISIBLE);
        fullNameProfile.setVisibility(View.INVISIBLE);
        nav_Menu.findItem(R.id.profile).setVisible(false);
        nav_Menu.findItem(R.id.logout).setVisible(false);
        nav_Menu.findItem(R.id.login).setVisible(true);

        if (Preferences.obtenerPreferenceString(getActivity(),
                Preferences.PREFERENCE_USER_LOGIN).length() > 0) {
            nav_Menu.findItem(R.id.profile).setVisible(true);
            nav_Menu.findItem(R.id.logout).setVisible(true);
            nav_Menu.findItem(R.id.login).setVisible(false);
            imageProfile.setVisibility(View.VISIBLE);
            nickProfile.setVisibility(View.VISIBLE);
            nickProfile.setText(Preferences.obtenerPreferenceString(getActivity(),
                    Preferences.PREFERENCE_USER_NICK));
            fullNameProfile.setVisibility(View.VISIBLE);
            fullNameProfile.setText(Preferences.obtenerPreferenceString(getActivity(),
                    Preferences.PREFERENCE_USER_FULL_NAME));
        }
    }

    // Peticiones a la API -------------------------------------------------------------------------

    public void mongoAPI(String url, String type) {
        final String URL_BASE = "https://api-rest-guia-miguelin-tfg.herokuapp.com/api";
        // Local http://192.168.1.106:1234/
        switch (type) {
            case ("USER"):
                new ContentMain.GetDataTask().execute(URL_BASE + url);
                break;
            case ("ESTABLISHMENTS"):
                new ContentMain.GetEstablishmentsTask().execute(URL_BASE + url);
                break;
        }
    }

    // GET user ------------------------------------------------------------------------------------

    @SuppressLint("StaticFieldLeak")
    class GetDataTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Obteniendo perfil para recomendarte...");
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
                //TODO idea del sistema de recomendación híbrido:
                //La idea es guardar los esblecimientos en un map donde key es la puntuación y value el establecimiento
                //1. Ordeno TODOS los establecimientos por nota media y les doy tantos puntos como nota media tengan
                //2. Miro el tipo de establecimientos que le gusta al usuario (eso es que le haya dado una nota de 3.5 o más) y a cada establecimiento de la lista de punto 1 que coincida con el tipo le sumo 4 puntos más
                // SEGUNDA PARTE
                //3. En los establecimientos que le gustan al usuario del punto 2, busco a qué otros usuarios también le ha gustado.
                //4. A esos usuarios les gustará otros establecimientos, a esos establecimientos se les sumará la multiplicacion de 0.5 por la puntuación que le hayan dado. Si el usuario recomendado sigue al usuario del que se está sacando información, el factor multiplicativo pasa a ser 0.75 puntos.
//                mongoAPI("/pubs/mostPopular?average=" + average, "ESTABLISHMENTS");

            } catch (Throwable throwable) {
                Toast.makeText(getActivity(), "Ha habido un problema al obtener la " +
                                "identificación del usuario", Toast.LENGTH_LONG).show();
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

    // GET establishments --------------------------------------------------------------------------

    @SuppressLint("StaticFieldLeak")
    class GetEstablishmentsTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Obteniendo los mejores establecimientos para ti...");
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
                if(pubs == null){
                    pubs = gson.fromJson(result, Establishment[].class);
                    mongoAPI("/restaurants/mostPopular?average=" + average,
                            "ESTABLISHMENTS");
                }else {
                    restaurants = gson.fromJson(result, Establishment[].class);

                    configAdapter();
                    configReclyclerView();
                    generateEstablishment();
                }

            } catch (Throwable throwable) {
                pubs = null;
                restaurants = null;
                // TODO esto peta cuando abro una pagina principal nuevamente...
//                Toast.makeText(getActivity(), "Ha habido un problema al obtener los " +
//                        "establecimientos", Toast.LENGTH_LONG).show();
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
