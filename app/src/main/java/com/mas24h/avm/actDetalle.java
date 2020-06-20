package com.mas24h.avm;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;

public class actDetalle extends AppCompatActivity implements View.OnClickListener {


    private TextView datReporte, datControl, datEstatus, datLat, datLng, lblRportPend, tvEstatusActual, datUsuario;
    ImageButton ibInformacion, ibMapa, ibArribo;
    ImageButton ibFotos;
    ImageButton ibPendiente;
    ImageButton ibCierre;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        recibirDatos();

        ibInformacion = (ImageButton)findViewById(R.id.ibInformacion);
        ibMapa = (ImageButton)findViewById(R.id.ibMapa);
        ibArribo = (ImageButton)findViewById(R.id.ibArribo);
        ibFotos = (ImageButton)findViewById(R.id.ibFotos);
        ibPendiente = (ImageButton)findViewById(R.id.ibPendiente);
        ibCierre = (ImageButton)findViewById(R.id.ibCierre);
        datControl = findViewById(R.id.datControl);

        datEstatus = findViewById(R.id.datEstatus);
        datLat = findViewById(R.id.datLat);
        datLng = findViewById(R.id.datLng);
        datReporte =(TextView) findViewById(R.id.datReporte);
        datUsuario =(TextView) findViewById(R.id.datUsuario);

        lblRportPend=(TextView)findViewById(R.id.lblRportPend);
        tvEstatusActual=(TextView)findViewById(R.id.tvEstatusActual);

        ibInformacion.setOnClickListener(this);
        ibMapa.setOnClickListener(this);
        ibArribo.setOnClickListener(this);
        ibFotos.setOnClickListener(this);
        ibPendiente.setOnClickListener(this);
        ibCierre.setOnClickListener(this);

        buscarReporte("https://www.mas-asistencia.com/operador/operavial42.php?reporte="+datReporte.getText()+"");

        colorTitulo();

    }
    private void recibirDatos(){
          Bundle extras = getIntent().getExtras();
          int intValue = extras.getInt("dato01", 0);
          String intStatus = extras.getString("dato02", String.valueOf(0));
          String user = extras.getString("dato03");
          datReporte =(TextView) findViewById(R.id.datReporte);
          datEstatus =(TextView) findViewById(R.id.datEstatus);
          datUsuario =(TextView) findViewById(R.id.datUsuario);
          datReporte.setText(String.valueOf(intValue));
          datEstatus.setText(String.valueOf(intStatus));
          datUsuario.setText(user);
    }

    private void colorTitulo(){

        String sta=datEstatus.getText().toString();

        if(sta.equals("4")){
            tvEstatusActual.setTextColor(tvEstatusActual.getContext().getResources().getColor(R.color.anaranjado));
            tvEstatusActual.setText("Pendiente de arribo");
        }else if(sta.equals("5")){
            tvEstatusActual.setTextColor(tvEstatusActual.getContext().getResources().getColor(R.color.azul));
            tvEstatusActual.setText("Reporte en servicio");
        }else if(sta.equals("7")){
            tvEstatusActual.setTextColor(tvEstatusActual.getContext().getResources().getColor(R.color.gris));
            tvEstatusActual.setText("Pendiente de cierre");
        }else{
            tvEstatusActual.setTextColor(tvEstatusActual.getContext().getResources().getColor(R.color.rojo));
            tvEstatusActual.setText("Reporte con error");
        }
    }

    @Override
    public void onClick(View v) {
        String stat=datEstatus.getText().toString();

        switch (v.getId()) {

            case R.id.ibInformacion:
                Intent intentInf= new Intent(actDetalle.this, actVisor.class);
                intentInf.putExtra("dato01", datControl.getText().toString());
                startActivity(intentInf);
                break;

            case R.id.ibMapa:

                String latitud=datLat.getText().toString();
                String longitud=datLng.getText().toString();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("geo:"+latitud+","+longitud+"?z=16&q="+latitud+","+longitud+"(Mexico)"));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);

                break;
            case R.id.ibArribo:
                if(stat.equals("4")){
                    arribo_alert();
                }else{
                    Toasty.info(this, "El reporte ya tiene arribo", Toast.LENGTH_LONG, false).show();
//                    Toast.makeText(this, "El reporte ya tiene arribo", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.ibFotos:
                Toast.makeText(getApplicationContext(),"Ya pelucas",Toast.LENGTH_LONG).show();
//                Intent fotos= new Intent(actDetalle.this, actFotos01.class);
//                fotos.putExtra("dato01", datControl.getText().toString());
//                startActivity(fotos);

                 break;
            case R.id.ibPendiente:

                if(stat.equals("5")){
                    pendiente_alert();
                }else if (stat.equals("7")){
                    Toasty.info(this, "El reporte ya esta en pendiente de cierre", Toast.LENGTH_LONG, false).show();
                   // Toast.makeText(this, "El reporte ya esta en pendiente de cierre", Toast.LENGTH_LONG).show();
                }else{
                    Toasty.info(this, "No se puede poner a pendiente en este momento, valide el arribo", Toast.LENGTH_LONG, false).show();
                //    Toast.makeText(this, "No se puede poner a pendiente en este momento, valide el arribo", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.ibCierre:
                if(stat.equals("7")){
                    Intent cierre= new Intent(this, actCierre.class );
                    cierre.putExtra("dato01", datControl.getText().toString());
                    cierre.putExtra("dato02", datUsuario.getText().toString());

                    startActivity(cierre);
                }else{
                    Toasty.info(this, "Antes de cerrar se debe enviar a pendiente", Toast.LENGTH_LONG, false).show();
//                    Toast.makeText(this, "Antes de cerrar se debe enviar a pendiente", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    private void buscarReporte(String URL) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        datControl.setText(jsonObject.getString("idControl"));
                        datLat.setText(jsonObject.getString("lat"));
                        datLng.setText(jsonObject.getString("lng"));

                    } catch (JSONException e) {
                        Toasty.info(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG, false).show();
//                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.info(getApplicationContext(), "Error de conexion", Toast.LENGTH_LONG, false).show();
              //  Toast.makeText(getApplicationContext(), "Error de conexion", Toast.LENGTH_LONG).show();
            }
        }
        );
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

    }

    private void arribo_alert(){
        final CharSequence[] opciones={"Confirmar arribo","Cancelar"};
        final AlertDialog.Builder alertOpciones= new AlertDialog.Builder(actDetalle.this);
        alertOpciones.setTitle("Arribo de reporte");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(opciones[i].equals("Confirmar arribo")){
                    arribo_asignar();
                }else{
                   dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }

    private void pendiente_alert(){
        final CharSequence[] opciones2={"Enviar a pendiente","Cancelar"};
        final AlertDialog.Builder alertOpciones2= new AlertDialog.Builder(actDetalle.this);
        alertOpciones2.setTitle("Confirmar pendiente de reporte");
        alertOpciones2.setItems(opciones2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(opciones2[i].equals("Enviar a pendiente")){
                    pendiente_asignar();
                }else{
                    dialogInterface.dismiss();
                }

            }
        });
        alertOpciones2.show();
    }

    private void arribo_asignar(){
        progressDialog = new ProgressDialog(actDetalle.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progres_dialog);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        String url = "https://mas-asistencia.com/operador/operavial43.php?control="+datControl.getText()+"&usr="+datUsuario.getText()+"";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toasty.info(getApplicationContext(), "Se asigno arribo al reporte", Toast.LENGTH_LONG, false).show();
                //Toast.makeText(getApplicationContext(), "Se asigno arribo al reporte", Toast.LENGTH_LONG).show();
                tvEstatusActual.setTextColor(tvEstatusActual.getContext().getResources().getColor(R.color.azul));
                tvEstatusActual.setText("Reporte en servicio");
                datEstatus.setText("5");
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.info(getApplicationContext(), error.toString(), Toast.LENGTH_LONG, false).show();
                //Toast.makeText(getApplicationContext(), "hubo un error", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                // TODO: Handle error

            }
        });
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void pendiente_asignar(){
        progressDialog = new ProgressDialog(actDetalle.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progres_dialog);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        String url = "https://mas-asistencia.com/operador/operavial47.php?control="+datControl.getText()+"&usr="+datUsuario.getText()+"";
        //String url = "https://mas-asistencia.com/operador/operavial47.php?control="+datControl.getText()+"";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Toasty.info(getApplicationContext(), "Se envio a pendiente el reporte", Toast.LENGTH_LONG, false).show();
               // Toast.makeText(getApplicationContext(), "Se envio a pendiente el reporte", Toast.LENGTH_LONG).show();
                tvEstatusActual.setTextColor(tvEstatusActual.getContext().getResources().getColor(R.color.gris));
                tvEstatusActual.setText("Pendiente de cierre");
                datEstatus.setText("7");
                progressDialog.dismiss();
                //tvEstatusActual.setText("Response: " + response.toString());
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.info(getApplicationContext(), "hubo un error", Toast.LENGTH_LONG, false).show();
//                Toast.makeText(getApplicationContext(), "hubo un error", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                // TODO: Handle error

            }
        });
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), actMain.class);
        intent.putExtra("dato01", datUsuario.getText().toString());
        startActivity(intent);
        finish();
    }
}
