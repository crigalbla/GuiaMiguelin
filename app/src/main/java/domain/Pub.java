package domain;

import java.util.List;

/**
 * Created by Cristian on 02/03/2018.
 */

public class Pub extends Establishment {

    private TypePub typePub;

    public Pub(String name, String address, String description, double latitud, double longitud,
               String closing, String opening, int phone, double average,
               List<String> reviews, String carte, TypePub typePub) {
        super(name, address, description, latitud, longitud, closing, opening, phone,
                average, reviews, carte);
        this.typePub = typePub;
    }

    public Pub(String name, String address, String description, double latitud, double longitud,
               String closing, String opening, int phone, double average, TypePub typePub) {
        super(name, address, description, latitud, longitud, closing, opening, phone,
                average);
        this.typePub = typePub;
    }

    public Pub(){

    }

    public TypePub getTypePub() {
        return typePub;
    }

    public void setTypePub(TypePub typePub) {
        this.typePub = typePub;
    }
}
