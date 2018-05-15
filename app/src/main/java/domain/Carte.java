package domain;

import java.util.List;

/**
 * Created by Cristian on 02/03/2018.
 */

public class Carte {

    private String establishment;
    private List<String> products;

    public Carte(String establishment, List<String> products){
        this.establishment = establishment;
        this.products = products;
    }

    public Carte(){
        
    }

    public String getEstablishment() {
        return establishment;
    }

    public void setEstablishment(String establishment) {
        this.establishment = establishment;
    }

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
    }
}
