import java.util.ArrayList;
import java.util.Collection;

public class Feature {
    private Double width = null;
    private Double height = null;

    private Collection<Region> whiteRegions;
    private Collection<Region> blackRegions;

    public Feature(Collection<Region> whiteRegions, Collection<Region> blackRegions) {
        this.whiteRegions = whiteRegions;
        this.blackRegions = blackRegions;
    }

    public Feature() {
    }

    public Collection<Region> getWhiteRegions() {
        return whiteRegions;
    }

    public void setWhiteRegions(Collection<Region> whiteRegions) {
        this.whiteRegions = whiteRegions;
    }

    public Collection<Region> getBlackRegions() {
        return blackRegions;
    }

    public void setBlackRegions(Collection<Region> blackRegions) {
        this.blackRegions = blackRegions;
    }

    public double getWidth() {

        if (this.width == null) {
            double max = 0.0;
            double width;

            for (Region p : whiteRegions) {
                width = p.getWidth();
                if (width > max)
                    max = width;
            }
            for (Region p : blackRegions) {
                width = p.getWidth();
                if (width > max)
                    max = width;
            }

            this.width = max;
        }

        return this.width;
    }

    public double getHeight() {

        if (height == null) {
            double max = 0.0;
            double height;

            for (Region p : whiteRegions) {
                height = p.getHeight();
                if (height > max)
                    max = height;
            }
            for (Region p : blackRegions) {
                height = p.getHeight();
                if (height > max)
                    max = height;
            }

            this.height = max;
        }
        return this.height;
    }


    public Feature scale(final int scaleCoefficient) throws IllegalArgumentException {
        /*
        * scaleCoefficient should be greater then 9 because primitives attributes are in a range between 0 and 1
        */
        if (scaleCoefficient < 10) {
            throw new IllegalArgumentException();
        }

        Collection<Region> white = new ArrayList<Region>();
        for (Region p : getWhiteRegions()) {
            white.add(p.scale(scaleCoefficient));
        }

        Collection<Region> black = new ArrayList<Region>();
        for (Region p : getBlackRegions()) {
            black.add(p.scale(scaleCoefficient));
        }

        Feature newFeature = new Feature(white, black);

        return newFeature;
    }


    @Override
    public String toString() {
        return "Feature{" +
                "whiteRegions=" + whiteRegions +
                ", blackRegions=" + blackRegions +
                '}';
    }
}
