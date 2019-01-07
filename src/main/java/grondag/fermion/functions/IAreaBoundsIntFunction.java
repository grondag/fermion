package grondag.fermion.functions;

@FunctionalInterface
public interface IAreaBoundsIntFunction {
    /**
     * Max values are inclusive.
     */
    int apply(int xMin, int yMin, int xMax, int yMax);
}