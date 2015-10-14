import com.sun.istack.internal.NotNull;

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

    public Primitive() {
        this.v1 = null;
        this.v2 = null;
        this.v3 = null;
        this.v4 = null;
    }

    public Point getV1() {
        return v1;
    }

    public Point getV2() {
        return v2;
    }

    public Point getV3() {
        return v3;
    }

    public Point getV4() {
        return v4;
    }

    @Override
    public String toString() {
        return "Primitive{" +
                "v1=" + v1 +
                ", v2=" + v2 +
                ", v3=" + v3 +
                ", v4=" + v4 +
                '}';
    }
}
