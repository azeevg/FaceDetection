import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Function;

/**
 * Created by vkokarev on 15.11.15.
 */
public final class Utils {
  private Utils(){}

  public static <T> double getMaxDimension(@NotNull final Collection<T> elems, @NotNull final Function<T, Double> dimension) {
    return elems.stream().map(dimension).max(Double::compareTo).get();
  }
}
