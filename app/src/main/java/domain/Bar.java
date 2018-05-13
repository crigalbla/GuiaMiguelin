package domain;

/**
 * Created by Cristian on 02/03/2018.
 */

public class Bar extends Establecimiento {

    private TipoBar tipoBar;

    public Bar(String nombre, String direccion, double latitud, double longitud, String cierre,
               String apertura, int telefono, double notaMedia, TipoBar tipoBar) {
        super(nombre, direccion, latitud, longitud, cierre, apertura, telefono, notaMedia);
        this.tipoBar = tipoBar;
    }

    public  Bar(){

    }

    public TipoBar getTipoBar() {
        return tipoBar;
    }

    public void setTipoBar(TipoBar tipoBar) {
        this.tipoBar = tipoBar;
    }
}
