package domain;

import java.util.List;

/**
 * Created by Cristian on 02/03/2018.
 */

public class Establishment {

    private String name;
    private String address;
    private String description;
    private double latitud;
    private double longitud;
    private String opening;
    private String closing;
    private int phone;
    private double average;
    private List<Review> reviews;
    private Carte carte;

    public Establishment(String name, String address, String description, double latitud,
                         double longitud, String opening, String closing, int phone,
                         double average, List<Review> reviews, Carte carte) {
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

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public Carte getCarte() {
        return carte;
    }

    public void setCarte(Carte carte) {
        this.carte = carte;
    }
}
