package grondag.fermion.functions;

public abstract class PrimitiveFunctions {
    @FunctionalInterface
    public interface IntToIntFunction {
        int apply(int i);
    }
}
