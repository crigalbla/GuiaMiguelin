package com.my.cristian.guiamiguelin;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
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
import domain.Pub;
import domain.Restaurant;
import domain.Review;
import domain.User;


public class ContentMain extends Fragment implements OnItemClickListener {

    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.swipe_recomendation)
    SwipeRefreshLayout swipeRecomendation;
    Unbinder unbinder;

    private static final Gson gson = new Gson();
    private User userLogged = null;
    private Pub[] pubs = null;
    private Restaurant[] restaurants = null;
    private Review[] reviews = null;
    private User[] users = null;
    private Integer numberPubsOnAdapter = 0;
    // Parámetros de configuración
    private String average = "4";
    private Double recomendedType = 4.0;
    private Double goodAverage = 2.5;
    private Double goodReview = 3.5;
    private Double factorNotFollow = 1.0;
    private Double factorFollow = 1.5;

    private EstablishmentAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        whatShow(); // Configuro el drawer para lo que debe mostrar y lo que no por defecto
        View view = inflater.inflate(R.layout.content_main, container, false);

        unbinder = ButterKnife.bind(this, view);
        // Esto es un listener para refrescar la vista
        swipeRecomendation.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        // Cuando cierro o abro sesión dejo a nulo todos los valores para que recomiende de nuevo
        if( (userLogged == null && Preferences.obtenerPreferenceString(getActivity(),
                Preferences.PREFERENCE_USER_LOGIN).length() > 0)
                ||
                (userLogged != null && Preferences.obtenerPreferenceString(getActivity(),
                        Preferences.PREFERENCE_USER_LOGIN).length() == 0) ){
            pubs = null;
            restaurants = null;
        }

        if(pubs == null && restaurants == null) {
            recomendationSystem();
        }else {
            configReclyclerView();
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

    // Método del swipeRefresh
    private void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pubs = null;
                restaurants = null;
                recomendationSystem();
                swipeRecomendation.setRefreshing(false);
            }
        }, 1000);
    }

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
        // Ordeno los establecimientos dando importancia a la media de valoraciones
        Arrays.sort(pubs);
        Arrays.sort(restaurants);

        if(userLogged == null){ // Recomiendo 4 en total
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

        }else{ // Caso en el que estoy logueado como usuario (recomiendo 6 en total)
            for (int i = 0; i < countPubs; i++) {
                // Para que no recomiende establecimientos donde ya se ha realizado una review
                if(pubs[i].getPTRS() >= 0) {
                    if (i < 3) {
                        adapter.add(pubs[i]);
                        numberPubsOnAdapter++;
                    }
                    if (i >= 3 && i < 6 - countRestaurants) {
                        adapter.add(pubs[i]);
                        numberPubsOnAdapter++;
                    }
                }
            }
            for (int i = 0; i < countRestaurants; i++) {
                // Para que no recomiende establecimientos donde ya se ha realizado una review
                if(restaurants[i].getPTRS() >= 0) {
                    if (i < 3)
                        adapter.add(restaurants[i]);
                    if (i >= 3 && i < 6 - countPubs)
                        adapter.add(restaurants[i]);
                }
            }
        }
    }

    private List<Establishment> recomendationSystem(){
        List<Establishment> recomended = new ArrayList<Establishment>();
        String userIdLogged = Preferences.obtenerPreferenceString(getActivity(),
                Preferences.PREFERENCE_USER_LOGIN);

        if(userIdLogged != null && userIdLogged.length() > 0){
            mongoAPI("/users/" + userIdLogged, "USER");
        }else{
            userLogged = null; // Esta línea es para el caso en el que cierro sesión
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
                new ContentMain.GetUserTask().execute(URL_BASE + url);
                break;
            case ("ESTABLISHMENTS"):
                new ContentMain.GetEstablishmentsTask().execute(URL_BASE + url);
                break;
            case ("REVIEWS"):
                new ContentMain.GetReviewsTask().execute(URL_BASE + url);
                break;
            case ("REVIEWS2"):
                new ContentMain.GetReviews2Task().execute(URL_BASE + url);
                break;
            case ("USERS"):
                new ContentMain.GetUsersTask().execute(URL_BASE + url);
                break;
        }
    }

    // GET user ------------------------------------------------------------------------------------

    @SuppressLint("StaticFieldLeak")
    class GetUserTask extends AsyncTask<String, Void, String> {

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
                mongoAPI("/pubs/", "ESTABLISHMENTS");

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
            progressDialog.setMessage("Buscado los mejores establecimientos...");
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
                    pubs = gson.fromJson(result, Pub[].class);
                    //1.1. Les doy/quito puntos en función de la nota media que tengan
                    for (int i = 0; i < pubs.length; i++) {
                        if (pubs[i].getReviews().size() > 0) {
                            pubs[i].setPTRS( (pubs[i].getAverage() - goodAverage)*2 );
                        }else {
                            pubs[i].setPTRS(0.0);
                        }
                    }

                    if(userLogged != null){ // Caso de sesión iniciada
                        mongoAPI("/restaurants/","ESTABLISHMENTS");
                    }else {
                        mongoAPI("/restaurants/mostPopular?average=" + average,
                                "ESTABLISHMENTS");
                    }
                }else {
                    restaurants = gson.fromJson(result, Restaurant[].class);
                    //1.1. Les doy puntos en función de la nota media que tengan
                    for (int i = 0; i < restaurants.length; i++){
                        if (restaurants[i].getReviews().size() > 0) {
                            restaurants[i].setPTRS( (restaurants[i].getAverage() - goodAverage)*2 );
                        }else {
                            restaurants[i].setPTRS(0.0);
                        }
                    }

                    if(userLogged != null){ // Caso de sesión iniciada
                        mongoAPI("/reviews/recomendationSystem/" + userLogged.getId(),
                                "REVIEWS");
                    }else{
                        configAdapter();
                        configReclyclerView();
                        generateEstablishment();
                    }
                }

            } catch (Throwable throwable) {
                // No puedo poner este primer toast porque al cerrar sesión estoy creando un nuevo
                // fragment y me peta...
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

    // GET reviews ---------------------------------------------------------------------------------

    @SuppressLint("StaticFieldLeak")
    class GetReviewsTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                reviews = gson.fromJson(result, Review[].class);
                String values = "";
                String types = "";

                //1.2. 'Elimino' TODOS los establecimientos a los que haya ido el usuario.
                for (int i = 0; i < pubs.length; i++) {
                    // Si ya ha hecho una review BUENA, guardame el tipo de establecimiento
                    for(Review rev: reviews) {
                        if (rev.getEstablishment().contains(pubs[i].getId())) {
                            pubs[i].setPTRS(-1000.0); // Para que nunca lo recomiende
                            // Si fue una buena review lo tendremos en cuenta en el punto 2
                            if (rev.getPuntuation() > goodReview) {
                                types = types + ", " + pubs[i].getTypePub().toString();
                                values = values + "-" + pubs[i].getId();
                            }
                            break;
                        }
                    }
                }
                for (int i = 0; i < restaurants.length; i++){
                    // Si ya ha hecho una review BUENA, guardame el tipo de establecimiento
                    for(Review rev: reviews)
                        if (rev.getEstablishment().contains(restaurants[i].getId())) {
                            restaurants[i].setPTRS(-1000.0); // Para que nunca lo recomiende
                            // Si fue una buena review lo tendremos en cuenta en el punto 2
                            if(rev.getPuntuation() > goodReview){
                                types = types + ", " + pubs[i].getTypePub().toString();
                                values = values + "-" + restaurants[i].getId();
                            }
                            break;
                        }
                }

                //2. Miro los tipos de establecimientos que le gustan al usuario (eso es que le
                // haya dado una nota mayor o igual que goodReview) y a cada establecimiento de la lista del
                // punto 1 que coincida con el tipo le sumo 4 puntos más
                for (int i = 0; i < pubs.length; i++)
                    if (types.contains(pubs[i].getTypePub().toString()))
                        pubs[i].setPTRS(pubs[i].getPTRS() + recomendedType);

                for (int i = 0; i < restaurants.length; i++)
                    if (types.contains(restaurants[i].getTypeRestaurant().toString()))
                        restaurants[i].setPTRS(restaurants[i].getPTRS() + recomendedType);

                // Si el usuario aún no ha creado reviews, pasa a mostrarle ya los establecimientos
                // que han sufrido modificaciones de puntos si el usuario sigue a alguien
                if(values.equals("")){
                    configAdapter();
                    configReclyclerView();
                    generateEstablishment();
                }else{
                    mongoAPI("/reviews/recomendationSystem?values=" + values, "REVIEWS2");
                }
            } catch (Throwable throwable) {
                Toast.makeText(getActivity(), "Ha habido un problema al obtener las reviews " +
                        "de los establecimientos para recomendar", Toast.LENGTH_LONG).show();
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

    // GET reviews2 --------------------------------------------------------------------------------

    @SuppressLint("StaticFieldLeak")
    class GetReviews2Task extends AsyncTask<String, Void, String> {

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
                reviews = gson.fromJson(result, Review[].class);

                //3. Para obtener las reviews de los establecimientos que me han gustado
                if(users == null){
                    String values = "";

                    for(Review r: reviews)
                        if(!values.contains(r.getAuthor())) {
                            if (values.length() > 0) {
                                values = values + "-" + r.getAuthor();
                            }else {
                                values = r.getAuthor();
                            }
                        }

                    mongoAPI("/users/recomendationSystem?values=" + values, "USERS");
                }else { //4. Para obtener las reviews de los usuarios con mis mismos gustos
                    Double factor = factorNotFollow;
                    Boolean entra = true;
                    for(Review rev: reviews){
                        for(Pub p: pubs){
                            if(rev.getEstablishment().equals(p.getId())){
                                if(userLogged.getReviews().contains(rev.getAuthor()))
                                    factor = factorFollow;
                                double morePTRS = (rev.getPuntuation() - goodAverage)*factor;
                                p.setPTRS(p.getPTRS() + morePTRS);
                                entra = false; // Para que coja la siguiente review
                                break;// Para que no siga buscando pubs si ya ha enoctrado la review
                            }
                        }
                        if(entra)
                        for(Restaurant r: restaurants){
                            if(rev.getEstablishment().equals(r.getId())){
                                if(userLogged.getReviews().contains(rev.getAuthor()))
                                    factor = 0.75;
                                double morePTRS = (rev.getPuntuation() - goodAverage)*factor;
                                r.setPTRS(r.getPTRS() + morePTRS);
                                break;// Para que no siga buscando rest si ya ha enoctrado la review
                            }
                        }
                    }
                    configAdapter();
                    configReclyclerView();
                    generateEstablishment();
                    Toast.makeText(getActivity(), "Hacer scroll para renovar recomendación",
                            Toast.LENGTH_LONG).show();
                }
            } catch (Throwable throwable) {
                Toast.makeText(getActivity(), "Ha habido un problema al obtener las reviews " +
                        "de los establecimientos para recomendar", Toast.LENGTH_LONG).show();
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

    // GET users -----------------------------------------------------------------------------------

    @SuppressLint("StaticFieldLeak")
    class GetUsersTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                users = gson.fromJson(result, User[].class);
                String values = "";
                for(User u: users)
                    values = values + "-" + u.getId();

                mongoAPI("/reviews/recomendationSystem2?values=", "REVIEWS2");
            } catch (Throwable throwable) {
                Toast.makeText(getActivity(), "Ha habido un problema al obtener los perfiles " +
                        "de usuario para recomendar", Toast.LENGTH_LONG).show();
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
