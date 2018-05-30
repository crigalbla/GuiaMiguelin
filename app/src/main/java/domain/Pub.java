package domain;

import java.util.List;

/**
 * Created by Cristian on 02/03/2018.
 */

public class Pub extends Establishment {

    private TypePub typePub;

    public Pub(String _id, String name, String address, String description, Double latitude,
               Double longitude, String closing, String opening, Integer phone, Double average,
               List<String> reviews, String carte, TypePub typePub) {
        super(_id, name, address, description, latitude, longitude, closing, opening, phone,
                average, reviews, carte);
        this.typePub = typePub;
    }

    public Pub(String _id, String name, String address, String description, Double latitude,
               Double longitude, String closing, String opening, Integer phone, Double average,
               TypePub typePub) {
        super(_id, name, address, description, latitude, longitude, closing, opening, phone,
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
