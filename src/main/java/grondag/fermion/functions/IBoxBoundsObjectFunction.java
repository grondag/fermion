package grondag.fermion.functions;

@FunctionalInterface
public interface IBoxBoundsObjectFunction<V> {
    V accept(int minX, int minY, int minZ, int maxX, int maxY, int maxZ);
}