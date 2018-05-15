package com.my.cristian.guiamiguelin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import domain.User;

public class Register extends AppCompatActivity {

    @BindView(R.id.send)
    Button send;
    @BindView(R.id.nick)
    EditText nick;
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.surnames)
    EditText surnames;
    @BindView(R.id.passaword)
    EditText passaword;
    @BindView(R.id.verifyPassword)
    EditText verifyPassword;
    @BindView(R.id.telephone)
    EditText telephone;
    @BindView(R.id.city)
    EditText city;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.pleasures)
    EditText pleasures;
    @BindView(R.id.description)
    EditText description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        ButterKnife.bind(this);
    }

    // Botones -------------------------------------------------------------------------------------

    @OnClick(R.id.send)
    public void onViewClicked() {
        // TODO comprobar si se ha registrado y poner un mensajito
        String textNick = nick.getText().toString();
        String textPassword = passaword.getText().toString();
        String textVeryPassoword = verifyPassword.getText().toString();
        String textName = name.getText().toString();
        String textSurnames = surnames.getText().toString();
        String textDescription = description.getText().toString();
        String textPleasures = pleasures.getText().toString();
        String textCity = city.getText().toString();
        String textEmail = email.getText().toString();
        Integer textTelephone = (telephone.getText().toString().length() > 0)? new Integer(telephone.getText().toString()) : null;

        User usuario = new User(nick.getText().toString(),  passaword.getText().toString(),
                name.getText().toString(), surnames.getText().toString(),
                description.getText().toString(), pleasures.getText().toString(),
                city.getText().toString(), email.getText().toString(),
                textTelephone, new ArrayList<String>(), new ArrayList<String>());
        Intent i = new Intent(Register.this, Login.class);
        startActivity(i);
    }
}
