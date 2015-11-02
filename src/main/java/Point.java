import org.jetbrains.annotations.NotNull;

public class Point extends Vector {
    public Point() {
        super();
    }

    public Point(final double x, final double y) {
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
