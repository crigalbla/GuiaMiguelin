package com.my.cristian.guiamiguelin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EstablishmentSearch extends AppCompatActivity {

    @BindView(R.id.search)
    EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.establishment_search);
        ButterKnife.bind(this);
    }

    // TODO hacer que busque de la base de datos
    @OnClick(R.id.searchButoon)
    public void onViewClicked() {

    }
}
