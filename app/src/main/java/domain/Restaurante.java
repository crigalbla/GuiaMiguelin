package domain;

/**
 * Created by Cristian on 02/03/2018.
 */

public class Restaurante extends Establecimiento {

    private TipoRestaurante tipoRestaurante;

    public Restaurante(String nombre, String direccion, double latitud, double longitud,
                       String apertura, String cierre, int telefono, double notaMedia,
                       TipoRestaurante tipoRestaurante) {
        super(nombre, direccion, latitud, longitud, cierre, apertura, telefono, notaMedia);
        this.tipoRestaurante = tipoRestaurante;
    }

    public Restaurante(String nombre, String direccion, double latitud, double longitud,
                       String apertura, String cierre, int telefono, double notaMedia) {
        super(nombre, direccion, latitud, longitud, cierre, apertura, telefono, notaMedia);
    }

    public TipoRestaurante getTipoRestaurante() {
        return tipoRestaurante;
    }

    public void setTipoRestaurante(TipoRestaurante tipoRestaurante) {
        this.tipoRestaurante = tipoRestaurante;
    }
}
