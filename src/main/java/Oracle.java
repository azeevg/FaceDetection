import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Oracle {
    public double verifyFace(@NotNull final List<Double> features);
}
