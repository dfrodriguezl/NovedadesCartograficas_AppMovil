package co.gov.dane.danevisor;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class login extends AppCompatActivity {


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.login);

        Button btn_login= (Button) findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EditText user_name= (EditText) findViewById(R.id.user_name);
                EditText user_password= (EditText) findViewById(R.id.user_password);
                TextView aviso_login=(TextView) findViewById(R.id.aviso_login);


                String usuario_nombre=user_name.getText().toString();
                String usuario_password=user_password.getText().toString();

                if(usuario_nombre.isEmpty() || usuario_password.isEmpty()){
                    aviso_login.setVisibility(View.VISIBLE);
                    aviso_login.setText("Datos incompletos");

                }else{

                }


            }
        });


    }
}