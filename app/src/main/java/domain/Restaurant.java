package domain;

import java.util.List;

/**
 * Created by Cristian on 02/03/2018.
 */

public class Restaurant extends Establishment {

    private TypeRestaurant typeRestaurant;

    public Restaurant(String _id, String name, String address, String description, Double latitude,
                      Double longitude, String opening, String closing, Integer phone, Double average,
                      List<String> reviews, String carte, TypeRestaurant typeRestaurant) {
        super(_id, name, address, description, latitude, longitude, closing, opening, phone, average,
                reviews, carte);
        this.typeRestaurant = typeRestaurant;
    }

    public Restaurant(String _id, String name, String address, String description, Double latitude,
                      Double longitude, String opening, String closing, Integer phone, Double average,
                      TypeRestaurant typeRestaurant) {
        super(_id, name, address, description, latitude, longitude, closing, opening, phone, average);
        this.typeRestaurant = typeRestaurant;
    }

    public Restaurant(){

    }

    // Borrar mas adelante cuando no haga falta
    public Restaurant(String _id, String name, String address, String description, Double latitude,
                      Double longitude, String opening, String closing, Integer phone, Double average,
                      List<String> reviews, String carte) {
        super(_id, name, address, description, latitude, longitude, closing, opening, phone, average,
                reviews, carte);
    }

    public TypeRestaurant getTypeRestaurant() {
        return typeRestaurant;
    }

    public void setTypeRestaurant(TypeRestaurant typeRestaurant) {
        this.typeRestaurant = typeRestaurant;
    }
}
