import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class FeatureHandler {
    private final int featureWindowSize;
    private final int step;
    private final List<Feature> features;

    public FeatureHandler(@NotNull final List<Feature> features, final int featureWindowSize) {
        this.features = features;
        this.featureWindowSize = featureWindowSize;
        step = this.featureWindowSize / 3;
    }

    public List<Double> getFeatureVector(@NotNull final BufferedImage image, @NotNull final Frame scanningWindow) {
        final IntegralImage integralImage = new IntegralImage(image);
        final List<Double> result = new ArrayList<>();
        features
                .stream()
                .map(f -> f.scale(featureWindowSize))
                .map(f -> handleFeature(f, integralImage, scanningWindow))
                .forEach(result::addAll);
        return result;
    }

    private List<Double> handleFeature(@NotNull final Feature scaledFeature,
                                       @NotNull final IntegralImage integralImage,
                                       @NotNull final Frame frame) {

        final List<Double> result = new ArrayList<>();
        final int cascadeWidth = (int) scaledFeature.getWidth();
        final int cascadeHeight = (int) scaledFeature.getHeight();

        final int topLeftY = (int) frame.getTopLeftCorner().getY();
        final int bottomRightY = (int) frame.getBottomRightCorner().getY();
        final int topLeftX = (int) frame.getTopLeftCorner().getX();
        final int bottomRightX = (int) frame.getBottomRightCorner().getX();

        if (topLeftY < 0 || topLeftX < 0 || bottomRightY - cascadeHeight < 0 || bottomRightX - cascadeWidth < 0)
            throw new IllegalArgumentException("Wrong ranges of frame");

        for (int i = topLeftY; i < (bottomRightY - cascadeHeight) / step; i++) {
            for (int j = topLeftX; j < (bottomRightX - cascadeWidth) / step; j++) {
                final Vector shift = new Vector(j * step, i * step);
                result.add(integralImage.handleCascade(scaledFeature, shift));
            }
        }

        return result;
    }
}
