import org.jetbrains.annotations.NotNull;

public class Frame {
    private final Point leftTopCorner;
    private final Point rightBottomCorner;

    public Frame(@NotNull final Point leftTopCorner, @NotNull final Point rightBottomCorner) {
        this.leftTopCorner = leftTopCorner;
        this.rightBottomCorner = rightBottomCorner;
    }

    public Point getLeftTopCorner() {
        return leftTopCorner;
    }

    public Point getRightBottomCorner() {
        return rightBottomCorner;
    }
}
