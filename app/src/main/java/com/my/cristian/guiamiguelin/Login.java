package com.my.cristian.guiamiguelin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Login extends AppCompatActivity {

    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.passaword)
    EditText passaword;
    @BindView(R.id.log_in)
    Button logIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);
        ButterKnife.bind(this);

//        Intent intent = getIntent();

    }

//    @Override
//    public void onBackPressed(){
//        setResult(RESULT_OK);
//        super.onBackPressed();
//    }

    @OnClick(R.id.log_in)
    public void loginOnClick() {
        Intent i = new Intent(Login.this, MainActivity.class);
        startActivity(i);
    }
}
