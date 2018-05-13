package com.my.cristian.guiamiguelin;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import domain.Restaurant;

/**
 * Created by Cristian on 02/03/2018.
 */

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private List<Restaurant> restaurantes;
    private Context context;
    private OnItemClickListener listener;

    public RestaurantAdapter(List<Restaurant> restaurantes, OnItemClickListener listener) {
        this.restaurantes = restaurantes;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.establishments, parent,
                false);
        this.context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Restaurant restaurante = restaurantes.get(position);

        holder.setListener(restaurante, listener);

        holder.nameEstablishment.setText(restaurante.getName());
        holder.adrresEstablishment.setText(restaurante.getAddress());
        holder.puntuation.setText(String.valueOf(restaurante.getAverage()));

    }

    @Override
    public int getItemCount() {
        return this.restaurantes.size();
    }

    public void add(Restaurant restaurante) {
        if (!restaurantes.contains(restaurante)) {
            restaurantes.add(restaurante);
            notifyDataSetChanged();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imgListEstablishment)
        AppCompatImageView imgListEstablishment;
        @BindView(R.id.nameEstablishment)
        AppCompatTextView nameEstablishment;
        @BindView(R.id.adrresEstablishment)
        AppCompatTextView adrresEstablishment;
        @BindView(R.id.puntuation)
        AppCompatTextView puntuation;
        @BindView(R.id.rlyEstablishment)
        RelativeLayout rlyEstablishment;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setListener(final Restaurant restaurante, final OnItemClickListener listener) {
            rlyEstablishment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(restaurante);
                }
            });


            rlyEstablishment.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onItemClick(restaurante);
                    return true;
                }
            });
        }
    }
}
