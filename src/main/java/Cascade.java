import java.util.ArrayList;
import java.util.Collection;
import java.util.DoubleSummaryStatistics;

public class Cascade {
    private Double width = null;
    private Double height = null;

    private Collection<Primitive> whitePrimitives;
    private Collection<Primitive> blackPrimitives;

    public Cascade(Collection<Primitive> whitePrimitives, Collection<Primitive> blackPrimitives) {
        this.whitePrimitives = whitePrimitives;
        this.blackPrimitives = blackPrimitives;
    }

    public Cascade() {
    }

    public Collection<Primitive> getWhitePrimitives() {
        return whitePrimitives;
    }

    public void setWhitePrimitives(Collection<Primitive> whitePrimitives) {
        this.whitePrimitives = whitePrimitives;
    }

    public Collection<Primitive> getBlackPrimitives() {
        return blackPrimitives;
    }

    public void setBlackPrimitives(Collection<Primitive> blackPrimitives) {
        this.blackPrimitives = blackPrimitives;
    }

    public double getWidth() {

        if (this.width == null) {
            double max = 0.0;
            double width;

            for (Primitive p : whitePrimitives) {
                width = p.getWidth();
                if (width > max)
                    max = width;
            }
            for (Primitive p : blackPrimitives) {
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

            for (Primitive p : whitePrimitives) {
                height = p.getHeight();
                if (height > max)
                    max = height;
            }
            for (Primitive p : blackPrimitives) {
                height = p.getHeight();
                if (height > max)
                    max = height;
            }

            this.height = max;
        }
        return this.height;
    }


    public Cascade scale(final int scaleCoefficient) throws IllegalArgumentException {
        /*
        * scaleCoefficient should be greater then 9 because primitives attributes are in a range between 0 and 1
        */
        if (scaleCoefficient < 10) {
            throw new IllegalArgumentException();
        }

        Collection<Primitive> white = new ArrayList<Primitive>();
        for (Primitive p : getWhitePrimitives()) {
            white.add(p.scale(scaleCoefficient));
        }

        Collection<Primitive> black = new ArrayList<Primitive>();
        for (Primitive p : getBlackPrimitives()) {
            black.add(p.scale(scaleCoefficient));
        }

        Cascade newCascade = new Cascade(white, black);

        return newCascade;
    }


    @Override
    public String toString() {
        return "Cascade{" +
                "whitePrimitives=" + whitePrimitives +
                ", blackPrimitives=" + blackPrimitives +
                '}';
    }
}
