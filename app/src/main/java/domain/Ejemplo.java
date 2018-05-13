package domain;

public class Ejemplo {

    private String name;
    private Integer likes;

    public Ejemplo(String name, Integer likes){
        this.name = name;
        this.likes = likes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }
}
