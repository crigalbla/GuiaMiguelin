package domain;

import java.util.List;

/**
 * Created by Cristian on 02/03/2018.
 */

public class Establishment implements Comparable<Establishment> {

    private String _id;
    private String name;
    private String address;
    private String description;
    private Double latitude;
    private Double longitude;
    private String opening;
    private String closing;
    private Integer phone;
    private Double average;
    private List<String> reviews;
    private String carte;
    //Atributos adicionales para poder hacer el sistema de recomendación
    private Double PTRS; //Puntuation to recomend system

    public Establishment(String _id, String name, String address, String description,
                         Double latitude, Double longitude, String opening, String closing,
                         Integer phone, Double average, List<String> reviews, String carte) {
        this._id = _id;
        this.name = name;
        this.address = address;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.opening = opening;
        this.closing = closing;
        this.phone = phone;
        this.average = average;
        this.reviews = reviews;
        this.carte = carte;
    }

    public Establishment(String _id, String name, String address, String description,
                         Double latitude, Double longitude, String opening, String closing,
                         Integer phone, Double average) {
        this._id = _id;
        this.name = name;
        this.address = address;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.opening = opening;
        this.closing = closing;
        this.phone = phone;
        this.average = average;
    }

    public Establishment(){

    }

    @Override
    public int compareTo(Establishment e) {
        if (PTRS > e.PTRS) {
            return -1;
        }
        if (PTRS < e.PTRS) {
            return 1;
        }
        return 0;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
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

    public Double getPTRS() {
        return PTRS;
    }

    public void setPTRS(Double PTRS) {
        this.PTRS = PTRS;
    }
}
