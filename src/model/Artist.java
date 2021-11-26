package model;

public class Artist {

    private final String name;

    public Artist(String name) {
        this.name = name != null ? name : "V.A";
    }

    @Override
    public String toString() {
        return name;
    }
}
