package domain;

import java.util.List;

/**
 * Created by Cristian on 02/03/2018.
 */

public class Restaurant extends Establishment {

    private TypeRestaurant typeRestaurant;

    public Restaurant(String name, String address, String description, double latitud,
                      double longitud, String opening, String closing, int phone, double average,
                      List<Review> reviews, Carte carte, TypeRestaurant tipoRestaurante) {
        super(name, address, description, latitud, longitud, closing, opening, phone, average,
                reviews, carte);
        this.typeRestaurant = typeRestaurant;
    }

    // Borrar mas adelante cuando no haga falta
    public Restaurant(String name, String address, String description, double latitud,
                      double longitud, String opening, String closing, int phone, double average,
                      List<Review> reviews, Carte carte) {
        super(name, address, description, latitud, longitud, closing, opening, phone, average,
                reviews, carte);
    }

    public TypeRestaurant getTypeRestaurant() {
        return typeRestaurant;
    }

    public void setTypeRestaurant(TypeRestaurant typeRestaurant) {
        this.typeRestaurant = typeRestaurant;
    }
}
