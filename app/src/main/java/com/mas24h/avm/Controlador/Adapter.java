package com.mas24h.avm.Controlador;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mas24h.avm.R;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.PlayerViewHolder> implements View.OnClickListener {


    private Context mCtx;
    private List<Folios> reportesList;
    private View.OnClickListener listener;

    // Provide a suitable constructor (depends on the kind of dataset)

    public Adapter(Context mCtx,List<Folios> reportesList) {
        this.mCtx = mCtx;
        this.reportesList = reportesList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_reportes, null);
        view.setOnClickListener(this);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Folios folio = reportesList.get(position);

        holder.textViewidReporte.setText(String.valueOf(folio.getidReporte()));
        holder.textViewEstatus.setText(folio.getEstatus());
        holder.textViewCalle.setText(folio.getCalle());
        holder.textViewColonia.setText(folio.getColonia());
        //holder.textViewColor.setText(folio.getEstatus());

        String sta= holder.textViewEstatus.getText().toString();
        //String sta=.getText().toString();
        if(sta.equals("9")){
            holder.textViewColor.setBackgroundColor(Color.parseColor("#FF76FF03"));
          //  Toast.makeText(this, sta, Toast.LENGTH_LONG).show();
        }else if(sta.equals("4")){
            holder.textViewColor.setBackgroundColor(Color.parseColor("#FF6F00"));
        }else if(sta.equals("5")){
            holder.textViewColor.setBackgroundColor(Color.parseColor("#3F51B5"));
        }else if(sta.equals("7")){
            holder.textViewColor.setBackgroundColor(Color.parseColor("#37474F"));
        }else {
            holder.textViewColor.setBackgroundColor(Color.parseColor("#F3072B"));
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() { return reportesList.size();  }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(listener!=null){
            listener.onClick(view);
        }
    }


    class PlayerViewHolder extends RecyclerView.ViewHolder {

        TextView textViewidReporte, textViewCalle, textViewColonia, textViewEstatus, textViewColor;

        public PlayerViewHolder(View itemView) {
            super(itemView);
            textViewidReporte = itemView.findViewById(R.id.textidReporte);
            textViewEstatus = itemView.findViewById(R.id.textStatus);
            textViewCalle = itemView.findViewById(R.id.textCalle);
            textViewColonia = itemView.findViewById(R.id.textColonia);
            textViewColor = itemView.findViewById(R.id.textColor);


        }
    }
}
