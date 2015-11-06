import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Region {

    private List<Point> vertexes;

    public Region(@NotNull final Point v1, @NotNull final Point v2,
                  @NotNull final Point v3, @NotNull final Point v4) {
        this.vertexes = Collections.unmodifiableList(Arrays.asList(v1, v2, v3, v4));
    }

    public Region() {
        vertexes = null;
    }

    public List<Point> getVertexes() {
        return vertexes;
    }

    public Region scale(final int scaleCoefficient) {
        Point[] newVertexes = new Point[4];
        List<Point> oldVertexes = getVertexes();
        int i = 0;
        for (Point p : oldVertexes) {
            newVertexes[i++] = Vector.multiply(p, scaleCoefficient).toPoint();
        }

        return new Region(newVertexes[0], newVertexes[1], newVertexes[2], newVertexes[3]);
    }

    public double getWidth() {
        double max = 0.0;

        for (Point p : vertexes) {
            if (p.getX() > max)
                max = p.getX();
        }

        return max;
    }

    public double getHeight() {
        double max = 0.0;

        for (Point p : vertexes) {
            if (p.getY() > max)
                max = p.getY();
        }

        return max;
    }

    @Override
    public String toString() {
        String result = "Region{vertexes=";
        for (Point p : vertexes) {
            result += p.toString() + " ";
        }
        return result + "}";
    }
}
