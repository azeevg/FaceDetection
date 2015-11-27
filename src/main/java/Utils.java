import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.Collection;
import java.util.function.Function;

/**
 * Created by vkokarev on 15.11.15.
 */
public final class Utils {
    private Utils() {
    }

    public static <T> double getMaxDimension(@NotNull final Collection<T> elems, @NotNull final Function<T, Double> dimension) {
        return elems.stream().map(dimension).max(Double::compareTo).get();
    }

    public static BufferedImage cloneBufferedImage(@NotNull final BufferedImage image) {
        ColorModel model = image.getColorModel();
        boolean isAlphaPremultiplied = model.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        return new BufferedImage(model, raster, isAlphaPremultiplied, null);
    }
}
