package com.my.cristian.guiamiguelin;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProfile extends AppCompatActivity {

    @BindView(R.id.etDescription)
    TextInputEditText etDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        ButterKnife.bind(this);

        new MongoDB().mongoAPI("/users/000000000000000000000500", "GET", this);
        etDescription.setText("hola mundo");
    }
}
