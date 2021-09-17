package Model;

/**
 *
 * @author Hoan
 */
public class Album {

    private final String name;

    public Album(String name) {
        this.name = name != null ? name : "";
    }

    @Override
    public String toString() {
        return name;
    }
}
