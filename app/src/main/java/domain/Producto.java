package domain;

/**
 * Created by Cristian on 02/03/2018.
 */

public class Producto {

    private String nombre;
    private double precio;
    private String tipoAlimento;
    private String tipoBebida;

    public Producto(String nombre, double precio, String tipoAlimento, String tipoBebida) {
        this.nombre = nombre;
        this.precio = precio;
        this.tipoAlimento = tipoAlimento;
        this.tipoBebida = tipoBebida;
    }

    public Producto(){

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getTipoAlimento() {
        return tipoAlimento;
    }

    public void setTipoAlimento(String tipoAlimento) {
        this.tipoAlimento = tipoAlimento;
    }

    public String getTipoBebida() {
        return tipoBebida;
    }

    public void setTipoBebida(String tipoBebida) {
        this.tipoBebida = tipoBebida;
    }
}
