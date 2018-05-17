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
import domain.User;

/**
 * Created by Cristian on 17/05/2018.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> users;
    private Context context;
    private OnItemClickListener2 listener;

    public UserAdapter(List<User> users, OnItemClickListener2 listener) {
        this.users = users;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users, parent,
                false);
        this.context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User user = users.get(position);

        holder.setListener(user, listener);

        holder.userNick.setText(user.getNick());
        holder.name.setText(user.getName());
        holder.surnames.setText(user.getSurname());

    }

    @Override
    public int getItemCount() {
        return this.users.size();
    }

    public void add(User user) {
        if (!users.contains(user)) {
            users.add(user);
            notifyDataSetChanged();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imgListUser)
        AppCompatImageView imgListUser;
        @BindView(R.id.userNick)
        AppCompatTextView userNick;
        @BindView(R.id.name)
        AppCompatTextView name;
        @BindView(R.id.surnames)
        AppCompatTextView surnames;
        @BindView(R.id.rlyUser)
        RelativeLayout rlyUser;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setListener(final User user, final OnItemClickListener2 listener) {
            rlyUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(user);
                }
            });


            rlyUser.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onItemClick(user);
                    return true;
                }
            });
        }
    }
}
