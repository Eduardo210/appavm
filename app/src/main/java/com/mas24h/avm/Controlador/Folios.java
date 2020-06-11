package com.mas24h.avm.Controlador;

public class Folios {

    private int idReporte;
    private String estatus;
    private String calle;
    private String colonia;

    public Folios(int idReporte, String estatus, String calle, String colonia) {
        //public Folios(String calle, String colonia) {
        this.idReporte = idReporte;
        this.estatus = estatus;
        this.calle = calle;
        this.colonia = colonia;
    }

    public int getidReporte() {
        return idReporte;
    }

    public String getEstatus() {
        return estatus;
    }
    public String getCalle() {
        return calle;
    }
    public String getColonia() {
        return colonia;
    }

}
