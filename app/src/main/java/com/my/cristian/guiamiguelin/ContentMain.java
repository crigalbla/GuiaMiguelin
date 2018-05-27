package com.my.cristian.guiamiguelin;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import domain.Establishment;
import domain.Restaurant;
import domain.TypeRestaurant;


public class ContentMain extends Fragment implements OnItemClickListener {

    @BindView(R.id.recycle)
    RecyclerView recycle;
    Unbinder unbinder;

    private EstablishmentAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_main, container, false);

        unbinder = ButterKnife.bind(this, view);
        RecyclerView a = recycle;
        configAdapter();
        configReclyclerView();
        generateRestaurant();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // Métodos auxiliares --------------------------------------------------------------------------

    @Override
    public void onItemClick(Establishment establishment) {
        Toast.makeText(getActivity(), "Acabas de pulsar en un establecimiento",
                Toast.LENGTH_LONG).show();
    }

    private void configAdapter() {
        adapter = new EstablishmentAdapter(new ArrayList<Establishment>(), this);
    }

    private void configReclyclerView() {
        recycle.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycle.setAdapter(adapter);
    }

    private void generateRestaurant() {
        // TODO meter aquí todos los establecimientos de la base de datos
        String[] nombres = {"bar", "restaurante", "bar2", "restaurante2"};
        String[] direcciones = {"dirección1", "dirección2", "dirección3", "direcciónDireción"};
        double[] latitudes = {1.0, 1.0, 1.0, 1.0};
        double[] longitudes = {1.0, 1.0, 1.0, 1.0};
        String[] cierres = {"22:00", "22:01", "23:00", "00:00"};
        String[] aperturas = {"22:00", "22:01", "23:00", "00:00"};
        int[] telefonos = {789456123, 987654321, 369258147, 147258369};
        double[] notasMedia = {5.0, 4.7, 2.3, 4.8};

        for (int i = 0; i < 4; i++) {
            Restaurant restaurante = new Restaurant("", nombres[i], direcciones[i],
                    "descripción", latitudes[i], longitudes[i], cierres[i], aperturas[i],
                    telefonos[i], notasMedia[i], new ArrayList<String>(), null,
                    TypeRestaurant.Buffet);
            adapter.add(restaurante);
        }
    }

    // Botones -------------------------------------------------------------------------------------

}
