
import java.util.Collection;

public class Cascade {
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

    @Override
    public String toString() {
        return "Cascade{" +
                "whitePrimitives=" + whitePrimitives +
                ", blackPrimitives=" + blackPrimitives +
                '}';
    }
}
