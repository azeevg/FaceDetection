import com.sun.istack.internal.NotNull;

import java.awt.*;

public class Primitive {
    private final Point v1;
    private final Point v2;
    private final Point v3;
    private final Point v4;

    public Primitive(@NotNull final Point v1, @NotNull final Point v2,
                     @NotNull final Point v3, @NotNull final Point v4) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4;
    }

    public Point[] getVertexes() {
        Point[] vertexes = new Point[] {v1, v2, v3, v4};
        return vertexes;
    }

}
