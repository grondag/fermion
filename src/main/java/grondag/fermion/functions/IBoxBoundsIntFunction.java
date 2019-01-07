package grondag.fermion.functions;

@FunctionalInterface
public interface IBoxBoundsIntFunction {
    int accept(int minX, int minY, int minZ, int maxX, int maxY, int maxZ);
}