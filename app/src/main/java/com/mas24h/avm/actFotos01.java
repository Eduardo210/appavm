package com.mas24h.avm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class actFotos01 extends AppCompatActivity {

    private TextView datControl;
    Button btnBuscar;
    LinearLayout lly1;
    LinearLayout lly2;
    LinearLayout lly3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotos01);

        datControl = (TextView)findViewById(R.id.datControl);
        lly1 = (LinearLayout)findViewById(R.id.lly1);
        lly2 = (LinearLayout)findViewById(R.id.lly2);
        lly3 = (LinearLayout)findViewById(R.id.lly3);
        recibirDatos();

        lly1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(actFotos01.this, actFotos10.class);
                intent.putExtra("dato01", datControl.getText().toString());
                startActivity(intent);
            }
        });

        lly2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(actFotos01.this, actFotos20.class);
                intent.putExtra("dato01", datControl.getText().toString());
                startActivity(intent);
            }
        });

        lly3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(actFotos01.this, actFotos30.class);
                intent.putExtra("dato01", datControl.getText().toString());
                startActivity(intent);
            }
        });
    }



    private void recibirDatos(){
        Bundle extras = getIntent().getExtras();
        String d1 = extras.getString("dato01");
        datControl = (TextView)findViewById(R.id.datControl);
        datControl.setText(d1);
    }
}
