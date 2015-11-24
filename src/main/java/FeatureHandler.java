import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class FeatureHandler {
    private final int scanningWindowSize;
    private final int step;
    private final List<Feature> features;

    public FeatureHandler(@NotNull final List<Feature> features, final int scanningWindowSize) {
        this.features = features;
        this.scanningWindowSize = scanningWindowSize;
        step = this.scanningWindowSize / 3;
    }

    public List<Double> getFeatureVector(@NotNull final BufferedImage image) {
        final IntegralImage integralImage = new IntegralImage(image);
        final List<Double> result = new ArrayList<>();
        features
                .stream()
                .map(f -> f.scale(scanningWindowSize))
                .map(f -> handleFeature(f, integralImage))
                .forEach(result::addAll);
        return result;
    }

    private List<Double> handleFeature(@NotNull final Feature scaledFeature, @NotNull final IntegralImage integralImage) {
        final List<Double> result = new ArrayList<>();
        final int cascadeWidth = (int) scaledFeature.getWidth();
        final int cascadeHeight = (int) scaledFeature.getHeight();


        for (int i = 0; i < (integralImage.getHeight() - cascadeHeight) / step; i++) {
            for (int j = 0; j < (integralImage.getWidth() - cascadeWidth) / step; j++) {
                final Vector shift = new Vector(j * step, i * step);
                result.add(integralImage.handleCascade(scaledFeature, shift));
            }
        }

        return result;
    }
}
