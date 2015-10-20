import org.jetbrains.annotations.NotNull;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Primitive {

    private List<Point> vertexes;

    public Primitive(@NotNull final Point v1, @NotNull final Point v2,
                     @NotNull final Point v3, @NotNull final Point v4) {
        this.vertexes = Collections.unmodifiableList(Arrays.asList(v1, v2, v3, v4));
    }

    public Primitive() {
        vertexes = null;
    }

    public List<Point> getVertexes() {
        return vertexes;
    }

    @Override
    public String toString() {
        String result = "Primitive{vertexes=";
        for (Point p : vertexes) {
            result += p.toString() + " ";
        }
        return result + "}";
    }
}
