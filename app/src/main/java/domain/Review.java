package domain;

/**
 * Created by Cristian on 02/03/2018.
 */

public class Review {

    private int puntuation;
    private String comment;
    private User author;
    private Establishment establishment;

    public Review(int puntuation, String comment, User author, Establishment establishment) {
        this.puntuation = puntuation;
        this.comment = comment;
        this.author = author;
        this.establishment = establishment;
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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Establishment getEstablishment() {
        return establishment;
    }

    public void setEstablishment(Establishment establishment) {
        this.establishment = establishment;
    }
}
