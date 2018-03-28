package com.my.cristian.guiamiguelin;

/**
 * Created by Cristian on 02/03/2018.
 */

public class Bar extends Establecimiento {

    private String tipoBar;

    public Bar(String nombre, String direccion, double latitud, double longitud, String cierre,
               String apertura, int telefono, double notaMedia, String tipoBar) {
        super(nombre, direccion, latitud, longitud, cierre, apertura, telefono, notaMedia);
        this.tipoBar = tipoBar;
    }

    public  Bar(){

    }

    public String getTipoBar() {
        return tipoBar;
    }

    public void setTipoBar(String tipoBar) {
        this.tipoBar = tipoBar;
    }
}
