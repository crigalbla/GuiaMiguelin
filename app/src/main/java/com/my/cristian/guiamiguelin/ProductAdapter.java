package com.my.cristian.guiamiguelin;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import domain.Product;

/**
 * Created by Cristian on 31/05/2018.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> products;
    private Context context;

    public ProductAdapter(List<Product> products) {
        this.products = products;
    }

    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products, parent,
                false);
        this.context = parent.getContext();
        return new ProductAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductAdapter.ViewHolder holder, int position) {
        final Product product = products.get(position);

        holder.productName.setText(product.getName());
        holder.price.setText(product.getPrice().toString() + "€");

        if(product.getTypeDrink() != null)
            holder.type.setText("Tipo de bebida: "+ product.getTypeDrink());
        if(product.getTypeFood() != null)
            holder.type.setText("Tipo de comida: " + product.getTypeFood());
    }

    @Override
    public int getItemCount() {
        return this.products.size();
    }

    public void add(Product product) {
        if (!products.contains(product)) {
            products.add(product);
            notifyDataSetChanged();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.productName)
        AppCompatTextView productName;
        @BindView(R.id.type)
        AppCompatTextView type;
        @BindView(R.id.price)
        AppCompatTextView price;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
