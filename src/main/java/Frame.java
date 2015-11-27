import org.jetbrains.annotations.NotNull;

public class Frame {
    private final Point topLeftCorner;
    private final Point bottomRightCorner;

    public Frame(@NotNull final Point topLeftCorner, @NotNull final Point bottomRightCorner) {
        this.topLeftCorner = topLeftCorner;
        this.bottomRightCorner = bottomRightCorner;

        if (topLeftCorner.getX() > bottomRightCorner.getX() || topLeftCorner.getY() > bottomRightCorner.getY())
            throw new IllegalArgumentException();
    }

    public Frame(final int x1, final int y1, final int x2, final int y2) {
        this.topLeftCorner = new Point(x1, y1);
        this.bottomRightCorner = new Point(x2, y2);

        if (x1 > x2 || y1 > y2)
            throw new IllegalArgumentException();
    }

    public Point getTopLeftCorner() {
        return topLeftCorner;
    }

    public Point getBottomRightCorner() {
        return bottomRightCorner;
    }
}
