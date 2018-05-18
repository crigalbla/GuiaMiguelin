package com.my.cristian.guiamiguelin;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import domain.Review;

/**
 * Created by Cristian on 18/05/2018.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<Review> reviews;
    private Context context;
    private OnItemClickListener listener;
    private List<String> ids = new ArrayList<String>();

    public ReviewAdapter(List<Review> reviews, OnItemClickListener listener) {
        this.reviews = reviews;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews, parent,
                false);
        this.context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Review review = reviews.get(position);

        holder.authorReview.setText(review.getAuthor());
        holder.puntuationReview.setText(review.getPuntuation());
        if(review.getComment() != null) {
            holder.commentReview.setText(review.getComment());
        }else {
            holder.commentReview.setText("");
        }

        ids.add(review.getId()); // Guardo el id
    }

    @Override
    public int getItemCount() {
        return this.reviews.size();
    }

    public void add(Review review) {
        if (!reviews.contains(review)) {
            reviews.add(review);
            notifyDataSetChanged();
        }
    }

    public String getId(Integer position){
        return ids.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.authorReview)
        AppCompatTextView authorReview;
        @BindView(R.id.puntuationReview)
        AppCompatTextView puntuationReview;
        @BindView(R.id.commentReview)
        AppCompatTextView commentReview;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
