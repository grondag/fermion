package grondag.fermion.structures;

public class NullHandler {
    public static <T> T defaultIfNull(final T checkedValue, final T defaultValue) {
        return checkedValue == null ? defaultValue : checkedValue;
    }
}
