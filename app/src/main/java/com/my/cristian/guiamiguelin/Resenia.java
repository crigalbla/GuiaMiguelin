package com.my.cristian.guiamiguelin;

/**
 * Created by Cristian on 02/03/2018.
 */

public class Resenia {

    private int puntuacion;
    private String comentario;

    public Resenia(int puntuacion, String comentario) {
        this.puntuacion = puntuacion;
        this.comentario = comentario;
    }

    public Resenia() {

    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
