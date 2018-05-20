package domain;

import java.util.List;

/**
 * Created by Cristian on 02/03/2018.
 */

public class Establishment {

    private String _id;
    private String name;
    private String address;
    private String description;
    private Double latitud;
    private Double longitud;
    private String opening;
    private String closing;
    private Integer phone;
    private Double average;
    private List<String> reviews;
    private String carte;

    public Establishment(String _id, String name, String address, String description, Double latitud,
                         Double longitud, String opening, String closing, Integer phone,
                         Double average, List<String> reviews, String carte) {
        this._id = _id;
        this.name = name;
        this.address = address;
        this.description = description;
        this.latitud = latitud;
        this.longitud = longitud;
        this.opening = opening;
        this.closing = closing;
        this.phone = phone;
        this.average = average;
        this.reviews = reviews;
        this.carte = carte;
    }

    public Establishment(String _id, String name, String address, String description, Double latitud,
                         Double longitud, String opening, String closing, Integer phone,
                         Double average) {
        this._id = _id;
        this.name = name;
        this.address = address;
        this.description = description;
        this.latitud = latitud;
        this.longitud = longitud;
        this.opening = opening;
        this.closing = closing;
        this.phone = phone;
        this.average = average;
    }

    public Establishment(){

    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getOpening() {
        return opening;
    }

    public void setOpening(String opening) {
        this.opening = opening;
    }

    public String getClosing() {
        return closing;
    }

    public void setClosing(String closing) {
        this.closing = closing;
    }

    public Integer getPhone() {
        return phone;
    }

    public void setPhone(Integer phone) {
        this.phone = phone;
    }

    public Double getAverage() {
        return average;
    }

    public void setAverage(Double average) {
        this.average = average;
    }

    public List<String> getReviews() {
        return reviews;
    }

    public void setReviews(List<String> reviews) {
        this.reviews = reviews;
    }

    public String getCarte() {
        return carte;
    }

    public void setCarte(String carte) {
        this.carte = carte;
    }
}
