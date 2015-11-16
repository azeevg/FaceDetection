import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Feature {
    private final double width;
    private final double height;

    private Collection<Region> whiteRegions;
    private Collection<Region> blackRegions;

    // we have only few scale coefficients, so lets cache features instead of calculating them on each iteration!
    private final Map<Integer, Feature> cache = new HashMap<>();

    public Feature(
        @JsonProperty("whiteRegions") final Collection<Region> whiteRegions,
        @JsonProperty("blackRegions") final Collection<Region> blackRegions) {
        this.whiteRegions = whiteRegions;
        this.blackRegions = blackRegions;
        this.width = Math.max(
            Utils.getMaxDimension(whiteRegions, Region::getWidth),
            Utils.getMaxDimension(blackRegions, Region::getWidth)
        );
        this.height = Math.max(
            Utils.getMaxDimension(whiteRegions, Region::getHeight),
            Utils.getMaxDimension(blackRegions, Region::getHeight)
        );
    }

    public Collection<Region> getWhiteRegions() {
        return Collections.unmodifiableCollection(whiteRegions);
    }


    public Collection<Region> getBlackRegions() {
        return Collections.unmodifiableCollection(blackRegions);
    }


    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    public Feature scale(final int scaleCoefficient) throws IllegalArgumentException {
        /*
        * scaleCoefficient should be greater then 9 because primitives attributes are in a range between 0 and 1
        */
        if (scaleCoefficient < 10) {
            throw new IllegalArgumentException();
        }

        cache.putIfAbsent(scaleCoefficient, new Feature(
            whiteRegions.stream().map(r -> r.scale(scaleCoefficient)).collect(Collectors.toList()),
            blackRegions.stream().map(r -> r.scale(scaleCoefficient)).collect(Collectors.toList())
        ));

        return cache.get(scaleCoefficient);
    }


    @Override
    public String toString() {
        return "Feature{" +
                "whiteRegions=" + whiteRegions +
                ", blackRegions=" + blackRegions +
                '}';
    }

}
