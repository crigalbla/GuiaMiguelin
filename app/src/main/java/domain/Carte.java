package domain;

import java.util.List;

/**
 * Created by Cristian on 02/03/2018.
 */

public class Carte {

    private Establishment establishment;
    private List<Product> products;

    public Carte(Establishment establishment, List<Product> products){
        this.establishment = establishment;
        this.products = products;
    }

    public Establishment getEstablishment() {
        return establishment;
    }

    public void setEstablishment(Establishment establishment) {
        this.establishment = establishment;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
