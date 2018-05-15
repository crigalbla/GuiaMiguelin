package domain;

/**
 * Created by Cristian on 02/03/2018.
 */

public class Review {

    private int puntuation;
    private String comment;
    private String author;
    private String establishment;

    public Review(int puntuation, String comment, String author, String establishment) {
        this.puntuation = puntuation;
        this.comment = comment;
        this.author = author;
        this.establishment = establishment;
    }

    public Review(int puntuation, String comment) {
        this.puntuation = puntuation;
        this.comment = comment;
    }

    public Review() {

    }

    public int getPuntuation() {
        return puntuation;
    }

    public void setPuntuation(int puntuation) {
        this.puntuation = puntuation;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEstablishment() {
        return establishment;
    }

    public void setEstablishment(String establishment) {
        this.establishment = establishment;
    }
}
