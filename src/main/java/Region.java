import org.codehaus.jackson.annotate.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Region {

    private final List<Point> vertexes;
    private final double width; //wtf rly double?
    private final double height;

    public Region(@JsonProperty("vertexes") final List<Point> vertexes) {
        this.vertexes = Collections.unmodifiableList(vertexes);
        if (this.vertexes.size() != 4) {
            throw new IllegalArgumentException("4 poits were expected");
        }
        this.width = Utils.getMaxDimension(this.vertexes, Point::getX);
        this.height = Utils.getMaxDimension(this.vertexes, Point::getY);
    }

    public Region(@NotNull final Point v1, @NotNull final Point v2,
                  @NotNull final Point v3, @NotNull final Point v4) {
        this(Arrays.asList(v1, v2, v3, v4));
    }

    public List<Point> getVertexes() {
        return vertexes;
    }

    public Region scale(final int scaleCoefficient) {
        final Point[] newVertexes = new Point[4];
        final List<Point> oldVertexes = getVertexes();
        int i = 0;
        for (final Point p : oldVertexes) {
            newVertexes[i++] = Vector.multiply(p, scaleCoefficient).toPoint();
        }

        return new Region(newVertexes[0], newVertexes[1], newVertexes[2], newVertexes[3]);
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
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
