package grondag.fermion.functions;

@FunctionalInterface
public interface IBoxBoundsIntConsumer {
    void accept(int minX, int minY, int minZ, int maxX, int maxY, int maxZ);
}