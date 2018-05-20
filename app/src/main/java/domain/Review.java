package domain;

/**
 * Created by Cristian on 02/03/2018.
 */

public class Review {

    private String _id;
    private Integer puntuation;
    private String comment;
    private String author;
    private String establishment;

    public Review(String _id, Integer puntuation, String comment, String author, String establishment) {
        this._id =_id;
        this.puntuation = puntuation;
        this.comment = comment;
        this.author = author;
        this.establishment = establishment;
    }

    public Review(String _id, Integer puntuation, String comment) {
        this._id =_id;
        this.puntuation = puntuation;
        this.comment = comment;
    }

    public Review() {

    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public Integer getPuntuation() {
        return puntuation;
    }

    public void setPuntuation(Integer puntuation) {
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
