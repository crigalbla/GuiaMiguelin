package com.my.cristian.guiamiguelin;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import domain.Ejemplo;

public class MongoAdapter extends ArrayAdapter {

    // Atributos
    private RequestQueue requestQueue;
    private static final String URL_BASE = "https://api-rest-guia-miguelin-tfg.herokuapp.com";
    private static final String URL_JSON = "/ejemplos";
    private static final String TAG = "Adapter";
    List<Ejemplo> items;
    JsonObjectRequest jsArrayRequest;

    public MongoAdapter(Context context) {
        super(context,0);

        // Crear nueva cola de peticiones
        requestQueue = Volley.newRequestQueue(context);

        // Nueva petición JSONObject
        jsArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL_BASE + URL_JSON,
                (JSONObject) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        items = parseJson(response);
                        notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error Respuesta en JSON: " + error.getMessage());

                    }
                }
        );

        // Añadir petición a la cola
        requestQueue.add(jsArrayRequest);
    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
//
//        // Referencia del view procesado
//        View listItemView;
//
//        //Comprobando si el View no existe
//        listItemView = null == convertView ? layoutInflater.inflate(
//                R.layout.post,
//                parent,
//                false) : convertView;
//
//
//        // Obtener el item actual
//        Ejemplo item = items.get(position);
//
//        // Obtener Views
//        TextView textoTitulo = (TextView) listItemView.
//                findViewById(R.id.textoTitulo);
//        TextView textoDescripcion = (TextView) listItemView.
//                findViewById(R.id.textoDescripcion);
//        final ImageView imagenPost = (ImageView) listItemView.
//                findViewById(R.id.imagenPost);
//
//        // Actualizar los Views
//        textoTitulo.setText(item.getTitulo());
//        textoDescripcion.setText(item.getDescripcion());

        // Petición para obtener la imagen
//        ImageRequest request = new ImageRequest(
//                URL_BASE + item.getImagen(),
//                new Response.Listener<Bitmap>() {
//                    @Override
//                    public void onResponse(Bitmap bitmap) {
//                        imagenPost.setImageBitmap(bitmap);
//                    }
//                }, 0, 0, null,null,
//                new Response.ErrorListener() {
//                    public void onErrorResponse(VolleyError error) {
//                        imagenPost.setImageResource(R.drawable.error);
//                        Log.d(TAG, "Error en respuesta Bitmap: "+ error.getMessage());
//                    }
//                });
//
//        // Añadir petición a la cola
//        requestQueue.add(request);


//        return listItemView;
//    }

    public List<Ejemplo> parseJson(JSONObject jsonObject){
        // Variables locales
        List<Ejemplo> posts = new ArrayList();
        JSONArray jsonArray = null;

        try {
            // Obtener el array del objeto
            jsonArray = jsonObject.getJSONArray("ejemplos");

            for(int i=0; i<jsonArray.length(); i++){

                try {
                    JSONObject objeto= jsonArray.getJSONObject(i);

                    Ejemplo ejemplo = new Ejemplo(
                            objeto.getString("name"),
                            objeto.getInt("likes")      // Existe el problema de que si el objeto no tiene un atributo, peta...
//                            objeto.getString("name"),
//                            objeto.getString("address"),
//                            objeto.getDouble("latitud"),
//                            objeto.getDouble("longitud"),
//                            objeto.getString("opening"),
//                            objeto.getString("closing"),
//                            objeto.getInt("phone"),
//                            objeto.getDouble("average")
                    );


                    posts.add(ejemplo);

                } catch (JSONException e) {
                    Log.e(TAG, "Error de parsing: "+ e.getMessage());
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return posts;
    }
}
