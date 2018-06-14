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
import domain.Establishment;

/**
 * Created by Cristian on 02/03/2018.
 */

public class EstablishmentAdapter extends RecyclerView.Adapter<EstablishmentAdapter.ViewHolder> {

    private List<Establishment> establishments;
    private Context context;
    private OnItemClickListener listener;

    public EstablishmentAdapter(List<Establishment> establishments, OnItemClickListener listener) {
        this.establishments = establishments;
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
        final Establishment establishment = establishments.get(position);

        holder.setListener(establishment, listener);

        holder.nameEstablishment.setText(establishment.getName());
        holder.adrresEstablishment.setText(establishment.getAddress());
        if(establishment.getReviews().size() > 0) {
            holder.puntuation.setText(String.valueOf(establishment.getAverage()));
        }else {
            holder.puntuation.setText("N/A");
        }

        holder.idEstablishment.setText(establishment.getId());
    }

    @Override
    public int getItemCount() {
        return this.establishments.size();
    }

    public void add(Establishment establishment) {
        if (!establishments.contains(establishment)) {
            establishments.add(establishment);
            notifyDataSetChanged();
        }
    }

    public String getId(Integer position){
        return establishments.get(position).getId();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imgListEstablishment)
        AppCompatImageView imgListEstablishment;
        @BindView(R.id.nameEstablishment)
        AppCompatTextView nameEstablishment;
        @BindView(R.id.adrresEstablishment)
        AppCompatTextView adrresEstablishment;
        @BindView(R.id.idEstablishment)
        AppCompatTextView idEstablishment;
        @BindView(R.id.puntuation)
        AppCompatTextView puntuation;
        @BindView(R.id.rlyEstablishment)
        RelativeLayout rlyEstablishment;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setListener(final Establishment establishment, final OnItemClickListener listener) {
            rlyEstablishment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(establishment);
                }
            });


            rlyEstablishment.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onItemClick(establishment);
                    return true;
                }
            });
        }
    }
}
