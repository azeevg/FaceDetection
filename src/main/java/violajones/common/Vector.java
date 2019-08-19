package violajones.common;

import org.jetbrains.annotations.NotNull;

public class Vector {
    private final double x;
    private final double y;

    public Vector() {
        x = 0;
        y = 0;
    }

    public Vector(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    public static Vector add(@NotNull final Vector v1, @NotNull final Vector v2) {
        return new Vector(v1.getX() + v2.getX(), v1.getY() + v2.getY());
    }

    public static Vector multiply(@NotNull final Vector v, final int k) {
        return new Vector(v.getX() * k, v.getY() * k);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Point toPoint() {
        return new Point(x, y);
    }

    @Override
    public String toString() {
        return "violajones.common.Vector{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
