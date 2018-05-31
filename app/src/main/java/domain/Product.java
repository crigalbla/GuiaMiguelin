package domain;

/**
 * Created by Cristian on 02/03/2018.
 */

public class Product {

    private String name;
    private Double price;
    private String typeFood;
    private String typeDrink;
    private String carte;

    public Product(String name, Double price, String typeFood, String typeDrink, String carte) {
        this.name = name;
        this.price = price;
        this.typeFood = typeFood;
        this.typeDrink = typeDrink;
        this.carte = carte;
    }

    public Product(String name, Double price, String typeFood, String typeDrink) {
        this.name = name;
        this.price = price;
        this.typeFood = typeFood;
        this.typeDrink = typeDrink;
    }

    public Product(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getTypeFood() {
        return typeFood;
    }

    public void setTypeFood(String typeFood) {
        this.typeFood = typeFood;
    }

    public String getTypeDrink() {
        return typeDrink;
    }

    public void setTypeDrink(String typeDrink) {
        this.typeDrink = typeDrink;
    }

    public String getCarte() {
        return carte;
    }

    public void setCarte(String carte) {
        this.carte = carte;
    }
}
