package com.mas24h.avm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;

public class actBuscar extends AppCompatActivity {

    EditText datReporte, edtControl;
    Button btnBuscar;
    TextView datControl;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        datReporte=(EditText)findViewById(R.id.datReporte);
        btnBuscar=(Button) findViewById(R.id.btnBuscar);

        datControl=(TextView) findViewById(R.id.datControl);


        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarProducto("http://www.mas-asistencia.com/operador/operavial46.php?reporte="+datReporte.getText()+"");
            }
        });
    }//termina create

    private void buscarProducto(String URL) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                    datControl.setText(jsonObject.getString("idControl"));
                    } catch (JSONException e) {
                        Toasty.info(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG, false).show();
                       // Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    Intent intent= new Intent(actBuscar.this, actVisor.class);
                    intent.putExtra("dato01", datControl.getText().toString());
                    startActivity(intent);
                   }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toasty.info(getApplicationContext(), "No se encontro el reporte", Toast.LENGTH_LONG, false).show();
                //Toast.makeText(getApplicationContext(), "No se encontro el reporte", Toast.LENGTH_LONG).show();
            }
        }
        );
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }


}
