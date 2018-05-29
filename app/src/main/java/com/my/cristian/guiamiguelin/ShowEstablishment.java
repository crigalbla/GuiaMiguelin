package com.my.cristian.guiamiguelin;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import domain.Pub;
import domain.Restaurant;
import domain.Review;
import domain.User;

public class ShowEstablishment extends Fragment {

    @BindView(R.id.establishmentName)
    TextView establishmentName;
    @BindView(R.id.establishmentAddresss)
    TextView establishmentAddresss;
    @BindView(R.id.establishmentHorary)
    TextView establishmentHorary;
    @BindView(R.id.establishmentDescription)
    TextView establishmentDescription;
    @BindView(R.id.establishmentType)
    TextView establishmentType;
    @BindView(R.id.establishmentPuntuation)
    AppCompatTextView establishmentPuntuation;
    @BindView(R.id.establishmentPhone)
    TextView establishmentPhone;
    @BindView(R.id.establishmentReviews)
    RecyclerView establishmentReviews;
    @BindView(R.id.noReviews)
    TextView noReviews;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;
    Unbinder unbinder;

    private static final Gson gson = new Gson();

    private Pub pub = null;
    private Restaurant restaurant = null;
    private User user = null;
    private int count2 = 0;

    private ReviewAdapter adapter;
    private Review[] reviews = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.show_establishment, container, false);
        unbinder = ButterKnife.bind(this, view);

        // Esto es un listener para refrescar la vista
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        if (pub != null && pub.getTypePub() != null) { // Casos en el que estoy pulsando back
            continuePub();
        } else if (restaurant != null && restaurant.getTypeRestaurant() != null) {
            continueRestaurant();
        } else { // Caso normal en el que entro por primera vez a la vista
            firstOrRecharge();
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // Métodos auxiliares --------------------------------------------------------------------------

    private void configAdapter() {
        adapter = new ReviewAdapter(new ArrayList<Review>());
    }

    private void configReclyclerView() {
        establishmentReviews.setLayoutManager(new LinearLayoutManager(getActivity()));
        establishmentReviews.setAdapter(adapter);
    }

    private void generateReviews() {
        for (Review r : reviews) {
            adapter.add(r);
        }
    }

    private void refreshContent(){
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                reviews = null;
                firstOrRecharge();
                swipeContainer.setRefreshing(false);
            }
        }, 1000);

    }

    private void firstOrRecharge(){
        String pubId = getArguments() != null ?
                (String) getArguments().get("pubId") : null;
        String restaurantId = getArguments() != null ?
                (String) getArguments().get("restaurantId") : null;

        if (pubId != null) {
            mongoAPI("/pubs/" + pubId, "GET");
        } else if (restaurantId != null) {
            mongoAPI("/restaurants/" + restaurantId, "GET");
        } else {
            Toast.makeText(getActivity(),
                    "No se ha obtenido ID de establecimiento para mostrar",
                    Toast.LENGTH_LONG).show();
        }
    }

    // Botones -------------------------------------------------------------------------------------

    @OnClick({R.id.weStateHere, R.id.newReview})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.weStateHere:
                // TODO
                Toast.makeText(getActivity(), "Por hacer", Toast.LENGTH_SHORT).show();
                break;
            case R.id.newReview:
                if (Preferences.obtenerPreferenceString(getActivity(),
                        Preferences.PREFERENCE_USER_LOGIN).length() > 0) {

                    getActivity().setTitle("Crear reseña");
                    Fragment fragment = new CreateReview();
                    Bundle args = new Bundle();
                    if (pub != null) {
                        args.putString("nombre_establecimiento", pub.getName());
                        args.putString("id_establecimiento", pub.getId());
                        args.putString("reseñas", pub.getReviews().toString());
                        args.putString("tipo", "/pubs/");
                        args.putString("nota_media", pub.getAverage().toString());
                    }
                    if (restaurant != null) {
                        args.putString("nombre_establecimiento", restaurant.getName());
                        args.putString("id_establecimiento", restaurant.getId());
                        args.putString("reseñas", restaurant.getReviews().toString());
                        args.putString("tipo", "/restaurants/");
                        args.putString("nota_media", restaurant.getAverage().toString());
                    }
                    fragment.setArguments(args);
                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.contenedor, fragment).addToBackStack(null).commit();
                } else {
                    Toast.makeText(getActivity(),
                            "Debes de iniciar sesión para poder dejar una reseña",
                            Toast.LENGTH_LONG).show();
                }
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
            case ("Reviews"):
                new GetReviewsTask().execute(URL_BASE + url);
                break;
            case ("User"):
                new GetAuthorTask().execute(URL_BASE + url);
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
            progressDialog.setMessage("Obteniendo establecimiento...");
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

            try { // Intenta parsear a obejto Pub
                pub = gson.fromJson(result, Pub.class);
                continuePub();

            } catch (Throwable throwable) { // Si la respuesta no es un objeto Pub
                try { // Intenta parsear a objeto Restaurant
                    restaurant = gson.fromJson(result, Restaurant.class);
                    continueRestaurant();

                } catch (Throwable throwable1) { // En caso de que haya habido error, notifícamelo
                    Toast.makeText(getActivity(),
                            "Ha habido un problema con la aplicación",
                            Toast.LENGTH_LONG).show();
                }
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

    private void continuePub() {
        String type = pub.getTypePub().toString();
        type = type.replaceAll("_", " ");
        establishmentName.setText(pub.getName());
        establishmentAddresss.setText(pub.getAddress());
        establishmentDescription.setText(pub.getDescription());
        establishmentHorary.setText("De " + pub.getOpening() + " a " + pub.getClosing());
        establishmentType.setText(type);
        if (pub.getReviews().size() > 0) {
            establishmentPuntuation.setText(String.valueOf(pub.getAverage()));
        } else {
            establishmentPuntuation.setText("N/A");
        }

        Integer phone = (pub.getPhone() != null) ? new Integer(pub.getPhone()) : null;

        if (phone != null)
            establishmentPhone.setText(phone.toString());

        if (pub.getReviews().size() > 0) {
            noReviews.setVisibility(View.INVISIBLE);
            if (reviews != null) { // Casos en el que estoy pulsando back
                configAdapter();
                configReclyclerView();
                generateReviews();
            } else {
                mongoAPI("/reviews/byEstablishment/" + pub.getId(), "Reviews");
            }
        }
    }

    private void continueRestaurant() {
        String type = restaurant.getTypeRestaurant().toString();
        type = type.replaceAll("_", " ");
        establishmentName.setText(restaurant.getName());
        establishmentAddresss.setText(restaurant.getAddress());
        establishmentDescription.setText(restaurant.getDescription());
        establishmentHorary.setText("De " + restaurant.getOpening() + " a "
                + restaurant.getClosing());
        establishmentType.setText(type);
        if (restaurant.getReviews().size() > 0) {
            establishmentPuntuation.setText(String.valueOf(restaurant.getAverage()));
        } else {
            establishmentPuntuation.setText("N/A");
        }

        Integer phone = (restaurant.getPhone() != null) ?
                new Integer(restaurant.getPhone()) : null;

        if (phone != null)
            establishmentPhone.setText(phone.toString());

        if (restaurant.getReviews().size() > 0) {
            noReviews.setVisibility(View.INVISIBLE);
            if (reviews != null) { // Casos en el que estoy pulsando back
                configAdapter();
                configReclyclerView();
                generateReviews();
            } else {
                mongoAPI("/reviews/byEstablishment/" + restaurant.getId(), "Reviews");
            }
        }
    }

    // GET Reviews ---------------------------------------------------------------------------------

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
                return "Error de conexión (2)";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                reviews = gson.fromJson(result, Review[].class);

                for (Review r : reviews)
                    mongoAPI("/users/" + r.getAuthor(), "User");

            } catch (Throwable throwable) {
                Toast.makeText(getActivity(),
                        "Ha habido un problema con la aplicación (2)",
                        Toast.LENGTH_LONG).show();
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

    // GET name of author --------------------------------------------------------------------------

    @SuppressLint("StaticFieldLeak")
    class GetAuthorTask extends AsyncTask<String, Void, String> {

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
                return "Error de conexión (3)";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                user = gson.fromJson(result, User.class);

                int count = 0;
                for (Review r : reviews) {
                    if (r.getAuthor().equals(user.getId())) {
                        r.setAuthor(user.getNick());
                        reviews[count] = r;
                        break;
                    }
                    count++;
                }
                count2++;

                if (count2 == reviews.length) {
                    configAdapter();
                    configReclyclerView();
                    generateReviews();
                    count2 = 0;
                }

            } catch (Throwable throwable) {
                Toast.makeText(getActivity(),
                        "Ha habido un problema con la aplicación (3)",
                        Toast.LENGTH_LONG).show();
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
