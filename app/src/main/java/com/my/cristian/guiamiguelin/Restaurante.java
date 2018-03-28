package com.my.cristian.guiamiguelin;

/**
 * Created by Cristian on 02/03/2018.
 */

public class Restaurante extends Establecimiento {

    private String tipoRestaurante;

    public Restaurante(String nombre, String direccion, double latitud, double longitud,
                       String cierre, String apertura, int telefono, double notaMedia) {
        super(nombre, direccion, latitud, longitud, cierre, apertura, telefono, notaMedia);
    }

    public Restaurante(String tipoRestaurante) {
        this.tipoRestaurante = tipoRestaurante;
    }

    public String getTipoRestaurante() {
        return tipoRestaurante;
    }

    public void setTipoRestaurante(String tipoRestaurante) {
        this.tipoRestaurante = tipoRestaurante;
    }
}
