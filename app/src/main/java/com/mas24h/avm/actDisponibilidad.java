package com.mas24h.avm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class actDisponibilidad extends AppCompatActivity {

    private static final String TAG = "actDisponibilidad";


    EditText mDisplayDate01, mDisplayDate02, mTimePicker01, mTimePicker02;
    TextView tv_user, tv_disp1, tv_statusAct;
    Button btn_disp1;

    DatePickerDialog.OnDateSetListener mDateSetListener01, mDateSetListener02;
    TimePickerDialog.OnTimeSetListener mOnTimeSetListener01, mOnTimeSetListener02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disponibilidad);

        mDisplayDate01 = (EditText)findViewById(R.id.et_fechaE);
        mDisplayDate02 = (EditText)findViewById(R.id.et_fechaS);
        mTimePicker01 = (EditText)findViewById(R.id.et_horaE);
        mTimePicker02 = (EditText)findViewById(R.id.et_horaS);

        tv_user = (TextView) findViewById(R.id.tv_user);
        tv_disp1 = (TextView) findViewById(R.id.tv_disp1);
        tv_statusAct =(TextView) findViewById(R.id.tv_statusAct);
        btn_disp1 = (Button) findViewById(R.id.btn_disp1);

        recibirDatos();

        buscarReporte("http://www.mas-asistencia.com/operador/operavial48.php?user="+tv_user.getText()+"");

       btn_disp1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               ejecutarServicio("https://mas-asistencia.com/operador/operavial44.php");
              }
        });

        mDisplayDate01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        actDisponibilidad.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener01,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDisplayDate02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        actDisponibilidad.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener02,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mTimePicker01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int Hour = cal.get(Calendar.HOUR_OF_DAY);
                int Minute = cal.get(Calendar.MINUTE);

                TimePickerDialog mTimePickerDialog = new TimePickerDialog(
                        actDisponibilidad.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mOnTimeSetListener01,
                        Hour,Minute, true);
                mTimePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mTimePickerDialog.show();
            }
        });

        mTimePicker02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int Hour = cal.get(Calendar.HOUR_OF_DAY);
                int Minute = cal.get(Calendar.MINUTE);

                TimePickerDialog mTimePickerDialog = new TimePickerDialog(
                        actDisponibilidad.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mOnTimeSetListener02,
                        Hour,Minute, true);
                mTimePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mTimePickerDialog.show();
            }
        });

        mDateSetListener01 = new DatePickerDialog.OnDateSetListener() {

            private String twoDigits(int n) {
                return (n<=9) ? ("0"+n) : String.valueOf(n);
            }
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                Log.d(TAG, "onDateSet: yyy-MM-dd: " + year + "-" + twoDigits(month) + "-" + day);
                String date = year + "-" + twoDigits(month) + "-" + day;
                mDisplayDate01.setText(date);
            }
        };

        mDateSetListener02 = new DatePickerDialog.OnDateSetListener() {

            private String twoDigits(int n) {
                return (n<=9) ? ("0"+n) : String.valueOf(n);
            }
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                Log.d(TAG, "onDateSet: yyy-MM-dd: " + year + "-" + twoDigits(month) + "-" + day);
                String date = year + "-" + twoDigits(month) + "-" + day;
                mDisplayDate02.setText(date);
            }
        };

        mOnTimeSetListener01 = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                String mTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                mTimePicker01.setText(mTime);
            }

        };

        mOnTimeSetListener02 = new TimePickerDialog.OnTimeSetListener() {
            @Override
                public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                String mTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                mTimePicker02.setText(mTime);
            }

        };
    }

    private void ejecutarServicio(String URL){


        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject respuesta = new JSONObject(response);
                    String msg = respuesta.getString("messages");
                    Toasty.info(getApplicationContext(), msg, Toast.LENGTH_LONG, false).show();
                    //Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toasty.info(getApplicationContext(), "Horario registrado", Toast.LENGTH_LONG, false).show();
                   // Toast.makeText(getApplicationContext(), "Horario registrado", Toast.LENGTH_LONG).show();
                    mDisplayDate01.setEnabled(false);
                    mDisplayDate02.setEnabled(false);
                    mTimePicker01.setEnabled(false);
                    mTimePicker02.setEnabled(false);
                    tv_disp1.setText("Tu disponibilidad esta activa");
                    btn_disp1.setEnabled(false);
                }
            }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.info(getApplicationContext(), error.toString(), Toast.LENGTH_LONG, false).show();
                //Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("user", tv_user.getText().toString());
                parametros.put("txtFechaEntrada", mDisplayDate01.getText().toString());
                parametros.put("txtHoraEntrada", mTimePicker01.getText().toString());
                parametros.put("txtFechaSalida", mDisplayDate02.getText().toString());
                parametros.put("txtHoraSalida", mTimePicker02.getText().toString());
                return parametros;
            }
            };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        }

    private void recibirDatos() {
        Bundle extras = getIntent().getExtras();
        String d1 = extras.getString("dato05");
        tv_user.setText(d1);
    }

    public void regresar(View view){
        Intent regresar= new Intent(this, actMain.class );
        startActivity(regresar);
    }

    private void buscarReporte(String URL) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        tv_statusAct.setText(jsonObject.getString("id"));

                        if(!tv_statusAct.getText().toString().equals("0")){
                            Toasty.info(getApplicationContext(), "Ya existe una disponibilidad activa", Toast.LENGTH_LONG, false).show();
                            //Toast.makeText(getApplicationContext(), "Ya existe una disponibilidad activa", Toast.LENGTH_LONG).show();
                            mDisplayDate01.setText(jsonObject.getString("fecha_ent"));
                            mTimePicker01.setText(jsonObject.getString("hora_ent"));
                            mDisplayDate02.setText(jsonObject.getString("fecha_sal"));
                            mTimePicker02.setText(jsonObject.getString("hora_sal"));
                            mDisplayDate01.setEnabled(false);
                            mDisplayDate02.setEnabled(false);
                            mTimePicker01.setEnabled(false);
                            mTimePicker02.setEnabled(false);
                            tv_disp1.setText("Tu disponibilidad esta activa");
                            btn_disp1.setEnabled(false);
                        }
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
            // Toast.makeText(getApplicationContext(), "Error de conexion", Toast.LENGTH_LONG).show();
            }
        }
        );
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }



}
