import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class PseudoOracle implements Oracle {

    @Override
    public double verifyFace(@NotNull List<Double> features) {
        int result = new Random().nextInt(1000);

        if (result < 10)
            return 1.0;
        else
            return -1.0;
    }
}
