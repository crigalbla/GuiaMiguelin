package com.my.cristian.guiamiguelin;

/**
 * Created by Cristian on 02/03/2018.
 */

public class Usuario {

    private String nick;
    private String passaword;
    private String name;
    private String apellidos;
    private String descripcion;
    private String gustos;
    private String ciudad;
    private String email;
    private int telefono;

    public Usuario(String nick, String passaword, String name, String apellidos, String descripcion,
                   String gustos, String ciudad, String email, int telefono) {
        this.nick = nick;
        this.passaword = passaword;
        this.name = name;
        this.apellidos = apellidos;
        this.descripcion = descripcion;
        this.gustos = gustos;
        this.ciudad = ciudad;
        this.email = email;
        this.telefono = telefono;
    }

    public Usuario(){

    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPassaword() {
        return passaword;
    }

    public void setPassaword(String passaword) {
        this.passaword = passaword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getGustos() {
        return gustos;
    }

    public void setGustos(String gustos) {
        this.gustos = gustos;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }
}
