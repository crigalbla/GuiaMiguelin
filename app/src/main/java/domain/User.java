package domain;

import java.util.List;

/**
 * Created by Cristian on 02/03/2018.
 */

public class User {

    private String _id;
    private String nick;
    private String password;
    private String name;
    private String surname;
    private String description;
    private String pleasures;
    private String city;
    private String email;
    private Integer phone;
    private List<String> followeds;
    private List<String> reviews;

    public User(String _id, String nick, String password, String name, String surname,
                String description, String pleasures, String city, String email, Integer phone,
                List<String> followeds, List<String> reviews) {
        this._id = _id;
        this.nick = nick;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.description = description;
        this.pleasures = pleasures;
        this.city = city;
        this.email = email;
        this.phone = phone;
        this.followeds = followeds;
        this.reviews = reviews;
    }

    public User(String _id, String nick, String password, String name, String surname,
                String description, String pleasures, String city, String email, Integer phone) {
        this._id = _id;
        this.nick = nick;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.description = description;
        this.pleasures = pleasures;
        this.city = city;
        this.email = email;
        this.phone = phone;
    }

    public User(){

    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPleasures() {
        return pleasures;
    }

    public void setPleasures(String pleasures) {
        this.pleasures = pleasures;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getPhone() {
        return phone;
    }

    public void setPhone(Integer phone) {
        this.phone = phone;
    }

    public List<String> getFolloweds() {
        return followeds;
    }

    public void setFolloweds(List<String> followeds) {
        this.followeds = followeds;
    }

    public List<String> getReviews() {
        return reviews;
    }

    public void setReviews(List<String> reviews) {
        this.reviews = reviews;
    }
}
