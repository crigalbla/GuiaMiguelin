package com.my.cristian.guiamiguelin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import domain.Pub;
import domain.Restaurant;

public class GoogleMaps extends Fragment implements OnMapReadyCallback {

    Unbinder unbinder;

    View view = null;
    private static int PETICION_PERMISO_LOCALIZACION = 101;
    private GoogleMap mMap;
    private Marker marker;
    double lat = 0.0;
    double lng = 0.0;
    String mensaje;
    Integer numberEstablishments = 0;
    Double cameraLat = 0.0;
    Double cameraLng = 0.0;
    boolean one = true;

    private static final Gson gson = new Gson();

    private Pub[] pubs = null;
    private Restaurant[] restaurants = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(view == null){
            view = inflater.inflate(R.layout.maps, container, false);
            unbinder = ButterKnife.bind(this, view);

            // Obtain the MapFragment and get notified when the map is ready to be used.
            MapFragment mapFragment = (MapFragment) this.getChildFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            mongoAPI("/pubs/", "PUBS");
            mongoAPI("/restaurants/", "RESTAURANTS");
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // Métodos de los mapas-------------------------------------------------------------------------

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Listener de los establecimientos en los mapas
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String coordinates = marker.getPosition().toString()
                        .replace("lat/lng: (", "")
                        .replace(")", "");
                Fragment fragment = new ShowEstablishment();
                Bundle args = new Bundle();
                if(marker.getSnippet().toString().contains("Restaurante")){
                    getActivity().setTitle("Restaurante");
                    args.putString("restaurantCoordinates", coordinates);
                }else if(marker.getSnippet().toString().contains("Bar")){
                    getActivity().setTitle("Bar");
                    args.putString("pubCoordinates", coordinates);
                }
                fragment.setArguments(args);
                getActivity().getFragmentManager().beginTransaction()
                        .replace(R.id.contenedor, fragment).addToBackStack(null).commit();
            }
        });

        myLocation();
    }

    // Activar los servicios del gps cuando esten apagados
    public void locationStart() {
        LocationManager mlocManager = (LocationManager) getActivity()
                .getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean a = this.equals(new GoogleMaps());
        if (!gpsEnabled) {
            buildAlertMessageNoGps();
        }
    }

    // Petición de activar GPS
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("El GPS está desactivado, ¿quieres activalor?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void agregateMarker(double lat, double lng) {
        LatLng coordinates = new LatLng(lat, lng);
        CameraUpdate myLocation = CameraUpdateFactory.newLatLngZoom(coordinates, 16);
        if (marker != null) marker.remove();
        marker = mMap.addMarker(new MarkerOptions()
                .position(coordinates));
//        mMap.animateCamera(myLocation); // con esto situo la cámara de nuevo en donde estoy
    }

    private void uppdateUbication(Location location){
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            agregateMarker(lat, lng);
        }
    }

    final LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            uppdateUbication(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {
            mensaje = ("GPS Activado");
            Mensaje();
        }

        @Override
        public void onProviderDisabled(String s) {
            mensaje = ("GPS Desactivado");
            locationStart();
            Mensaje();
        }
    };

    private void myLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PETICION_PERMISO_LOCALIZACION);
            return;
        } else {
            LocationManager locationManager = (LocationManager) getActivity()
                    .getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            uppdateUbication(location);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0,locListener);
        }
    }

    public void Mensaje() {
        Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
    }

    // Peticiones a la API -------------------------------------------------------------------------

    public void mongoAPI(String url, String type) {
        final String URL_BASE = "https://api-rest-guia-miguelin-tfg.herokuapp.com/api";
        // Local http://192.168.1.106:1234/
        switch (type) {
            case ("PUBS"):
                new GoogleMaps.GetPubsTask().execute(URL_BASE + url);
                break;
            case ("RESTAURANTS"):
                new GoogleMaps.GetRestaurantsTask().execute(URL_BASE + url);
                break;
        }
    }

    // GET pubs-------------------------------------------------------------------------------------

    @SuppressLint("StaticFieldLeak")
    class GetPubsTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Obteniendo bares...");
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
                pubs = gson.fromJson(result, Pub[].class);

                String coordinatesByShowEstablishment = getArguments() != null ?
                        (String) getArguments().get("coordinates") : null;
                // Añado todos lo bares al mapa
                for(Pub p: pubs){
                    String average = "N/A";
                    if(p.getReviews().size() > 0)
                        average = p.getAverage().toString();

                    LatLng latLng = new LatLng(p.getLatitude(), p.getLongitude());
                    Marker ma = mMap.addMarker(new MarkerOptions().position(latLng)
                            .title(p.getName()));
                    ma.setSnippet("Tipo de Bar: " + p.getTypePub()
                            + ", Puntuación: " + average);
                    cameraLat = cameraLat + p.getLatitude();
                    cameraLng = cameraLng + p.getLongitude();

                    // Compruebo si vengo de la vista showEstablishment para desplegar el marker
                    // del establecimiento
                    if(one && coordinatesByShowEstablishment != null &&
                            coordinatesByShowEstablishment.equals(p.getLatitude()
                                    + "," + p.getLongitude())){
                        ma.showInfoWindow();
                        one = false;
                    }
                }
                numberEstablishments = pubs.length;

            } catch (Throwable throwable) {// En caso de que haya habido error, notifícamelo
                Toast.makeText(getActivity(),
                        "Ha habido un problema al obtener los bares",
                        Toast.LENGTH_LONG).show();
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

    // GET restaurants------------------------------------------------------------------------------

    @SuppressLint("StaticFieldLeak")
    class GetRestaurantsTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Obteniendo restaurantes...");
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
                restaurants = gson.fromJson(result, Restaurant[].class);

                String coordinatesByShowEstablishment = getArguments() != null ?
                        (String) getArguments().get("coordinates") : null;
                // Añado todos los restaurantes al mapa
                for(Restaurant r: restaurants){
                    String average = "N/A";
                    if(r.getReviews().size() > 0)
                        average = r.getAverage().toString();

                    LatLng latLng = new LatLng(r.getLatitude(), r.getLongitude());
                    Marker ma = mMap.addMarker(new MarkerOptions().position(latLng)
                            .title(r.getName()));
                    ma.setSnippet("Tipo de Restaurante: " + r.getTypeRestaurant()
                            + ", Puntuación: " + average);
                    cameraLat = cameraLat + r.getLatitude();
                    cameraLng = cameraLng + r.getLongitude();

                    // Compruebo si vengo de la vista showEstablishment para desplegar el marker
                    // del establecimiento
                    if(one && coordinatesByShowEstablishment != null &&
                            coordinatesByShowEstablishment.equals(r.getLatitude()
                                    + "," + r.getLongitude())){
                        ma.showInfoWindow();
                        one = false;
                    }
                }
                numberEstablishments = numberEstablishments + restaurants.length;

                // Caso en el que vengo desde la vista showEstablishment
                if(coordinatesByShowEstablishment != null){
                    String[] coord = coordinatesByShowEstablishment.split(",");
                    LatLng camera = new LatLng(new Double(coord[0]), new Double(coord[1]));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camera, 20));
                }else { // Caso normal cargando la vista GoogleMaps
                    // Coloco la cámara en el centro de todos los establecimientos
                    LatLng camera = new LatLng(cameraLat/numberEstablishments,
                            cameraLng/numberEstablishments);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camera, 16));
                }

            } catch (Throwable throwable) {// En caso de que haya habido error, notifícamelo
                Toast.makeText(getActivity(),
                        "Ha habido un problema al obtener los restaurantes",
                        Toast.LENGTH_LONG).show();
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
