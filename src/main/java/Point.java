import org.codehaus.jackson.annotate.JsonProperty;

public class Point extends Vector {
    public Point() {
        super();
    }

    public Point(@JsonProperty("x") final double x, @JsonProperty("y")final double y) {
        super(x, y);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + getX() +
                ", y=" + getY() +
                '}';
    }
}
