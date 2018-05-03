package domain;

/**
 * Created by Cristian on 02/03/2018.
 */

public class Establecimiento {

    private String nombre;
    private String direccion;
    private double latitud;
    private double longitud;
    private String cierre;
    private String apertura;
    private int telefono;
    private double notaMedia;

    public Establecimiento(String nombre, String direccion, double latitud, double longitud,
                           String cierre, String apertura, int telefono, double notaMedia) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.cierre = cierre;
        this.apertura = apertura;
        this.telefono = telefono;
        this.notaMedia = notaMedia;
    }

    public Establecimiento() {

    }

    public String getNombre() {
        return nombre;
    }

    public void setName(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getCierre() {
        return cierre;
    }

    public void setCierre(String cierre) {
        this.cierre = cierre;
    }

    public String getApertura() {
        return apertura;
    }

    public void setApertura(String apertura) {
        this.apertura = apertura;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public double getNotaMedia() {
        return notaMedia;
    }

    public void setNotaMedia(double notaMedia) {
        this.notaMedia = notaMedia;
    }
}
