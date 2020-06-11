package com.mas24h.avm.Controlador;

public class solicitudes {

    private int id;
    //private String name;
    private String solicitudTipo;
    //public Frutas(){}
    public solicitudes(){}
    public solicitudes(int id, String solicitudTipo){
        this.setId(id);
        this.setsolicitudTipo(solicitudTipo);
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getsolicitudTipo() {
        return solicitudTipo;
    }
    public void setsolicitudTipo(String solicitudTipo) {
        this.solicitudTipo = solicitudTipo;
    }
}
